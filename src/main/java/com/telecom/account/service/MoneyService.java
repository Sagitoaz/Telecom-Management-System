package com.telecom.account.service;

import com.telecom.account.dto.CreditRequest;
import com.telecom.account.dto.FinancialResult;
import com.telecom.account.entity.ServiceAccount;
import com.telecom.account.model.TxnDirection;
import com.telecom.account.model.TxnSourceType;
import com.telecom.account.repository.FinancialTransactionDAO;
import com.telecom.account.repository.ServiceAccountDAO;
import com.telecom.operations.dto.OperationResult;
import com.telecom.operations.model.*;
import com.telecom.operations.repository.OperationRequestDAO;
import com.telecom.operations.service.OperationClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Service
public class MoneyService {

    private final ServiceAccountDAO accountDAO;
    private final MoneyCore moneyCore;
    private final DataSource dataSource;
    private final FinancialTransactionDAO financialTxnDAO;
    private final OperationRequestDAO operationRequestDAO;
    private final OperationClaimService operationClaimService;

    public MoneyService(
            ServiceAccountDAO accountDAO,
            MoneyCore moneyCore,
            DataSource dataSource,
            FinancialTransactionDAO financialTxnDAO,
            OperationRequestDAO operationRequestDAO,
            OperationClaimService operationClaimService
        ) {
        this.accountDAO = accountDAO;
        this.moneyCore = moneyCore;
        this.dataSource = dataSource;
        this.financialTxnDAO = financialTxnDAO;
        this.operationRequestDAO = operationRequestDAO;
        this.operationClaimService = operationClaimService;
    }

    public OperationResult creditMoney(
            UUID accountId,
            CreditRequest request,
            String callerId
    ) throws SQLException {

        RequestKey key = new RequestKey(
                callerId,
                OperationName.CREDIT,
                request.requestId()
        );

        Map<String, Object> payload = Map.of(
                "accountId", accountId.toString(),
                "subscriberId", request.subscriberId().toString(),
                "amountVnd", request.amountVnd()
        );

        Claim claim = operationClaimService.claim(key, payload);

        // Check request status
        if (claim.outcome() == ClaimOutcome.REPLAY) {
            return claim.existingResult();
        }
        if (claim.outcome() == ClaimOutcome.PROCESSING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Operation is still processing");
        }

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                OperationStatus operationStatus = operationRequestDAO.findStatusByIdForUpdate(connection, claim.operationRequestId());

                if (operationStatus != OperationStatus.PROCESSING) {
                    throw new IllegalStateException("Operation is not in PROCESSING status");
                }

                ServiceAccount account = accountDAO.findByIdForUpdate(connection, accountId);

                // Check account
                if (account == null) {
                    return failCredit(
                            connection,
                            request,
                            claim.operationRequestId(),
                            "ACCOUNT_NOT_FOUND",
                            "Account not found"
                    );
                }
                if (!account.getSubscriberId().equals(request.subscriberId())) {
                    return failCredit(
                            connection,
                            request,
                            claim.operationRequestId(),
                            "SUBSCRIBER_MISMATCH",
                            "Account does not belong to subscriber"
                    );
                }
                if (!account.getStatus().equals("ACTIVE")) {
                    return failCredit(
                            connection,
                            request,
                            claim.operationRequestId(),
                            "ACCOUNT_NOT_ACTIVE",
                            "Account is not active"
                    );
                }

                long oldBalance = account.getBalanceVnd();
                long newBalance = 0;
                try {
                    newBalance = moneyCore.calculateCredit(oldBalance, request.amountVnd());
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().equals("Invalid balance")) {
                        throw e;
                    }

                    return failCredit(
                            connection,
                            request,
                            claim.operationRequestId(),
                            "CREDIT_REJECTED",
                            e.getMessage()
                    );
                } catch (ArithmeticException e) {
                    return failCredit(
                            connection,
                            request,
                            claim.operationRequestId(),
                            "BALANCE_LIMIT_EXCEEDED",
                            "Balance limit exceeded"
                    );
                }

                int update = accountDAO.updateBalance(
                        connection,
                        account.getId(),
                        account.getVersion(),
                        newBalance
                );

                if (update != 1) {
                    throw new IllegalArgumentException("Update account balance failed");
                }

                //Insert financial transaction
                UUID txnId = UUID.randomUUID();

                LocalDateTime eligibilityCheckedAt = LocalDateTime.now(ZoneOffset.UTC);

                int insertedRows = financialTxnDAO.insertTransaction(
                        connection,
                        txnId,
                        claim.operationRequestId(),
                        account.getId(),
                        account.getSubscriberId(),
                        null,
                        null,
                        TxnSourceType.CREDIT_TOPUP.name(),
                        TxnDirection.CREDIT.name(),
                        request.amountVnd(),
                        oldBalance,
                        newBalance,
                        eligibilityCheckedAt,
                        "CREDIT:" + claim.operationRequestId()
                );

                if (insertedRows != 1) {
                    throw new IllegalStateException("Insert financial transaction failed");
                }

                FinancialResult financialResult = new FinancialResult(
                        txnId,
                        account.getId(),
                        request.amountVnd(),
                        oldBalance,
                        newBalance
                );

                OperationResult result = new OperationResult(
                        request.requestId(),
                        OperationName.CREDIT,
                        claim.operationRequestId().toString(),
                        OperationStatus.SUCCEEDED,
                        "CREDIT_SUCCESS",
                        "Credit completed successfully",
                        OffsetDateTime.now(ZoneOffset.UTC),
                        financialResult,
                        null,   // subscription
                        null,   // cancelAcceptedAt
                        null,   // cancelEffectedAt
                        null    // receiptCheckedAt
                );

                int completedRows = operationRequestDAO.completeOperation(
                        connection,
                        claim.operationRequestId(),
                        result
                );

                if (completedRows != 1) {
                    throw new IllegalStateException("Complete operation failed");
                }

                connection.commit();

                return result;

            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    private OperationResult failCredit(
            Connection connection,
            CreditRequest request,
            UUID operationId,
            String code,
            String message
    ) throws SQLException {
        OperationResult result = new OperationResult(
                request.requestId(),
                OperationName.CREDIT,
                operationId.toString(),
                OperationStatus.FAILED,
                code,
                message,
                OffsetDateTime.now(ZoneOffset.UTC),
                null, // financial
                null, // subscription
                null, // cancelAcceptedAt
                null, // cancelEffectedAt
                null  // receiptCheckedAt
        );

        int rows = operationRequestDAO.completeOperation(
                connection,
                operationId,
                result
        );

        if (rows != 1) {
            throw new IllegalStateException(
                    "Complete failed operation failed"
            );
        }

        connection.commit();

        return result;
    }
}

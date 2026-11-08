package com.telecom.account.repository;

import com.telecom.account.entity.FinancialTransaction;
import com.telecom.account.model.TxnDirection;
import com.telecom.account.model.TxnSourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {

    @Modifying
    @NativeQuery("""
    INSERT INTO financial_transaction (
        id,
        operation_request_id,
        account_id,
        subscriber_id,
        subscription_id,
        subscription_cycle_id,
        source_type,
        direction,
        amount_vnd,
        balance_before,
        balance_after,
        recorded_at,
        eligibility_checked_at,
        logical_key
    ) VALUES (
        :id,
        :operation_request_id,
        :account_id,
        :subscriber_id,
        :subscription_id,
        :subscription_cycle_id,
        :source_type,
        :direction,
        :amount_vnd,
        :balance_before,
        :balance_after,
        UTC_TIMESTAMP(6),
        :eligibility_checked_at,
        :logical_key
        )
    """)
    int insertTransaction(
            @Param("id") UUID id,
            @Param("operation_request_id") UUID operID,
            @Param("account_id") UUID accountId,
            @Param("subscriber_id") UUID subscriberId,
            @Param("subscription_id") UUID subscriptionId,
            @Param("subscription_cycle_id") UUID subscriptionCycleId,
            @Param("source_type") String source_type,
            @Param("direction") String direction,
            @Param("amount_vnd") long amoount,
            @Param("balance_before") long oldBalance,
            @Param("balance_after") long newBalance,
            @Param("eligibility_checked_at") LocalDateTime eligibilityCheckedAt,
            @Param("logical_key") String logicalKey
    );
}

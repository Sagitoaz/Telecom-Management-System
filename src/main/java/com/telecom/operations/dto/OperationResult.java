package com.telecom.operations.dto;

import com.telecom.account.dto.FinancialResult;
import com.telecom.operations.model.OperationName;
import com.telecom.operations.model.OperationStatus;
import com.telecom.subscription.dto.Subscription;

import java.time.OffsetDateTime;

public record OperationResult(
        String requestId,
        OperationName operation,
        String correlationId,
        OperationStatus status,
        String code,
        String message,
        OffsetDateTime completedAt,
        FinancialResult financial,
        Subscription subscription,
        OffsetDateTime cancelAcceptedAt,
        OffsetDateTime cancelEffectedAt,
        OffsetDateTime receiptCheckedAt
) {}

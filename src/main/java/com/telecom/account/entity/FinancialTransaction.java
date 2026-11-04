package com.telecom.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transaction")
public class FinancialTransaction {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "operation_request_id", length = 36)
    private String operationRequestId;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    @Column(name = "subscriber_id", length = 36, nullable = false)
    private String subcriberId;

    @Column(name = "subscription_id", length = 36)
    private String subscriptionId;

    @Column(name = "subscription_cycle_id", length = 36)
    private String subscriptionCycleId;

    @Column(name = "source_type", length = 24, nullable = false)
    private String sourceType;

    @Column(name = "direction", length = 6, nullable = false)
    private String direction;

    @Column(name = "amount_vnd", nullable = false)
    private long amountVnd;

    @Column(name = "balance_before", nullable = false)
    private long balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "eligibility_checked_at", nullable = false)
    private LocalDateTime eligibilityCheckedAt;

    @Column(name = "logical_key", length = 160, nullable = false)
    private String logicalKey;

    protected FinancialTransaction() {}
}

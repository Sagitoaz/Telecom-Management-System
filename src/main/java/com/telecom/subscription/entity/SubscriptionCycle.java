package com.telecom.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_cycle")
public class SubscriptionCycle {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "subscription_id", length = 36, nullable = false)
    private UUID subscriptionId;

    @Column(name = "cycle_key", length = 128, nullable = false)
    private String cycleKey;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(name = "price_vnd", nullable = false)
    private long priceVnd;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "catalog_version", nullable = false)
    private long catalogVersion;

    @Column(name = "fee_transaction_id", length = 36, nullable = false)
    private String feeTransactionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected SubscriptionCycle() {}
}

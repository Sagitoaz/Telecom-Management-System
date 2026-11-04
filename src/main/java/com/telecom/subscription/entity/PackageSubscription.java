package com.telecom.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "package_subscription")
public class PackageSubscription {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "subscriber_id", length = 36, nullable = false)
    private String subscriberId;

    @Column(name = "package_id", length = 36, nullable = false)
    private String packageId;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "state", length = 20, nullable = false)
    private String state;

    @Column(name = "coverage_end")
    private LocalDateTime coverageEnd;

    @Column(name = "auto_renew", nullable = false)
    private boolean autoRenew;

    @Column(name = "cancel_effective_at")
    private LocalDateTime cancelEffectiveAt;

    @Column(name = "cancel_accepted_at")
    private LocalDateTime cancelAcceptedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected PackageSubscription() {}
}

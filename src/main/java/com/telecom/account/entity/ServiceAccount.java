package com.telecom.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_account")
public class ServiceAccount {
    @Id
    @Getter
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "subscriber_id", length = 36, nullable = false)
    private UUID subscriberId;

    @Column(name = "account_type", length = 32, nullable = false)
    private String accountType;

    @Getter
    @Column(name = "balance_vnd", nullable = false)
    private long balanceVnd;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "status", length = 16, nullable = false)
    private String status;

    @Getter
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ServiceAccount() {}

}

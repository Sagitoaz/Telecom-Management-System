package com.telecom.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "phone_number_inventory")
public class PhoneNumberInventory {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "msisdn", length = 16, nullable = false)
    private String msisdn;

    @Column(name = "status", length = 12, nullable = false)
    private String status;

    @Column(name = "random_key", nullable = false)
    private double randomKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    protected PhoneNumberInventory() {}
}

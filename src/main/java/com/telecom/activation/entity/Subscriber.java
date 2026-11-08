package com.telecom.activation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriber")
public class Subscriber {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "customer_id", length = 36, nullable = false)
    private UUID customerId;

    @Column(name = "phone_number_id", length = 36, nullable = false)
    private UUID phoneNumberId;

    @Column(name = "status", length = 16, nullable = false)
    private String status;

    @Column(name = "activated_at", nullable = false)
    private LocalDateTime activatedAt;

    protected Subscriber() {}
}

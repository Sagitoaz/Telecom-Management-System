package com.telecom.activation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriber")
public class Subscriber {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @Column(name = "phone_number_id", length = 36, nullable = false)
    private String phoneNumberId;

    @Column(name = "status", length = 16, nullable = false)
    private String status;

    @Column(name = "activated_at", nullable = false)
    private LocalDateTime activatedAt;

    protected Subscriber() {}
}

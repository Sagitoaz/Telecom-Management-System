package com.telecom.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verified_document")
public class VerifiedDocument {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "customer_id", length = 36, nullable = false)
    private UUID customerId;

    @Column(name = "document_type", length = 12, nullable = false)
    private String documentType;

    @Column(name = "normalized_number", length = 64, nullable = false)
    private String normalizedNumber;

    @Column(name = "verification_ref", nullable = false)
    private String verificationRef;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected VerifiedDocument() {}
}

package com.telecom.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_package")
public class ServicePackage {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "code", length = 64, nullable = false)
    private String code;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "catalog_version", nullable = false)
    private long catalogVersion;

    @Column(name = "price_vnd", nullable = false)
    private long priceVnd;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "sale_status", length = 16, nullable = false)
    private String saleStatus;

    @Column(name = "grandfather_renewal", nullable = false)
    private boolean grandfatherRenewal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ServicePackage() {}
}

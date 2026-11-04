package com.telecom.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "occupation", length = 120)
    private String occupation;

    @Column(name = "contact", length = 120)
    private String contact;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Customer() {}
}

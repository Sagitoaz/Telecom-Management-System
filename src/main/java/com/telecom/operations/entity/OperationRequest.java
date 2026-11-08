package com.telecom.operations.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "operation_request")
public class OperationRequest {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "caller_id", length = 128, nullable = false)
    private String callerId;

    @Column(name = "request_id", length = 128, nullable = false)
    private String requestId;

    @Column(name = "operation", length = 24, nullable = false)
    private String operation;

    @Column(name = "fingerprint", length = 64, nullable = false)
    private String fingerprint;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "canonical_payload", columnDefinition = "json")
    private Map<String, Object> canonicalPayload;

    @Column(name = "status", length = 12, nullable = false)
    private String status;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "eligibility_checked_at")
    private LocalDateTime eligibilityCheckedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "replay_until")
    private LocalDateTime replayUntil;

    @Column(name = "result_code", length = 64)
    private String resultCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_summary", columnDefinition = "json")
    private JsonNode resultSummary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_payload", columnDefinition = "json")
    private JsonNode responsePayload;

    @Column(name = "lease_owner", length = 128)
    private String leaseOwner;

    @Column(name = "lease_until")
    private LocalDateTime leaseUntil;

    @Column(name = "retry_at")
    private LocalDateTime retryAt;

    @Column(name = "fence", nullable = false)
    private long fence;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    protected OperationRequest() {}
}

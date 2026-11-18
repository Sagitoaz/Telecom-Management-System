package com.telecom.operations.entity;

import com.telecom.operations.model.OperationName;
import com.telecom.operations.model.OperationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "operation_request")
public class OperationRequest {
    @Id
    @Getter
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Getter
    @Column(name = "caller_id", length = 128, nullable = false)
    private String callerId;

    @Getter
    @Column(name = "request_id", length = 128, nullable = false)
    private String requestId;

    @Getter
    @Column(name = "operation", length = 24, nullable = false)
    private String operation;

    @Getter
    @Column(name = "fingerprint", length = 64, nullable = false)
    private String fingerprint;

    @Getter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "canonical_payload", columnDefinition = "json")
    private Map<String, Object> canonicalPayload;

    @Getter
    @Column(name = "status", length = 12, nullable = false)
    private String status;

    @Getter
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

    @Getter
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

    public static OperationRequest newRequest(
            String callerId,
            String requestId,
            OperationName operation,
            String fingerprint,
            Map<String, Object> payload,
            LocalDateTime receivedAt
    ) {
        OperationRequest op = new OperationRequest();
        op.id = UUID.randomUUID();
        op.callerId = Objects.requireNonNull(callerId);
        op.requestId= Objects.requireNonNull(requestId);
        op.operation = Objects.requireNonNull(operation).name();
        op.fingerprint = Objects.requireNonNull(fingerprint);
        op.canonicalPayload = new LinkedHashMap<>(Objects.requireNonNull(payload));
        op.status = OperationStatus.PROCESSING.name();
        op.receivedAt = Objects.requireNonNull(receivedAt);
        op.fence = 0L;
        op.attemptCount = 0;

        return op;
    }
}

package com.telecom.operations.service;

import com.telecom.operations.entity.OperationRequest;
import com.telecom.operations.model.RequestKey;
import com.telecom.operations.repository.OperationRequestRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class OperationClaimWriter {
    private final OperationRequestRepository operationRepo;
    private final JdbcTemplate jdbcTemplate;

    public OperationClaimWriter(OperationRequestRepository operationRepo, JdbcTemplate jdbcTemplate) {
        this.operationRepo = operationRepo;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public UUID insertProcessing(
            RequestKey key,
            String fingerprint,
            Map<String, Object> payload
    ) {
        LocalDateTime receivedAt = jdbcTemplate.queryForObject(
                "SELECT UTC_TIMESTAMP(6)",
                LocalDateTime.class
        );
        if (receivedAt == null) {
            throw new IllegalStateException("Could not read database time");
        }

        OperationRequest op = OperationRequest.newRequest(
                key.callerId(),
                key.requestId(),
                key.operation(),
                fingerprint,
                payload,
                receivedAt
        );

        int rows = operationRepo.insertProcessing(
                op.getId(),
                op.getCallerId(),
                op.getRequestId(),
                op.getOperation(),
                op.getFingerprint(),
                CanonicalPayload.canonicalize(op.getCanonicalPayload()),
                op.getStatus(),
                op.getReceivedAt()
        );

        if (rows != 1) {
            throw new IllegalStateException("Insert to operation request fail");
        }

        return op.getId();
    }
}

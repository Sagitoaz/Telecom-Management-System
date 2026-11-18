package com.telecom.operations.service;

import com.telecom.operations.dto.OperationResult;
import com.telecom.operations.entity.OperationRequest;
import com.telecom.operations.exception.IdempotencyConflictException;
import com.telecom.operations.model.Claim;
import com.telecom.operations.model.ClaimOutcome;
import com.telecom.operations.model.OperationStatus;
import com.telecom.operations.model.RequestKey;
import com.telecom.operations.repository.OperationRequestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Service
public class OperationClaimService {

    private final OperationRequestRepository operationRepo;
    private final OperationClaimWriter writer;
    private final ObjectMapper mapper;

    public OperationClaimService(
            OperationRequestRepository operationRepo,
            OperationClaimWriter writer,
            ObjectMapper mapper
    ) {
        this.operationRepo = operationRepo;
        this.writer = writer;
        this.mapper = mapper;
    }

    public Claim claim(RequestKey key, Map<String, Object> payload) {
        String canonical = CanonicalPayload.canonicalize(payload);
        String fingerprint = CanonicalPayload.sha256(canonical);

        try {
            UUID operationId = writer.insertProcessing(key, fingerprint, payload);

            return new Claim(operationId, ClaimOutcome.NEW, null);
        } catch (DataIntegrityViolationException e) {
            if (!isMySqlDuplicateKey(e)) {
                throw e;
            }

            OperationRequest existing = operationRepo.findByKey(key).orElseThrow(() -> e);

            if (!fingerprint.equalsIgnoreCase(existing.getFingerprint())) {
                throw new IdempotencyConflictException("IDEMPOTENCY_CONFLICT");
            }

            OperationStatus status = OperationStatus.valueOf(existing.getStatus());

            return switch (status) {
                case PROCESSING -> new Claim(existing.getId(), ClaimOutcome.PROCESSING, null);
                case SUCCEEDED, FAILED -> {
                    OperationResult result = mapper.treeToValue(existing.getResponsePayload(), OperationResult.class);
                    yield new Claim(existing.getId(), ClaimOutcome.REPLAY, result);
                }
            };
        }
    }

    private boolean isMySqlDuplicateKey(DataIntegrityViolationException e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof SQLException sql) {
                if (sql.getErrorCode() == 1062) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }
}

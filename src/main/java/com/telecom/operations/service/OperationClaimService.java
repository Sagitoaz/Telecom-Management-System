package com.telecom.operations.service;

import com.telecom.operations.dto.OperationResult;
import com.telecom.operations.exception.IdempotencyConflictException;
import com.telecom.operations.model.*;
import com.telecom.operations.repository.OperationRequestDAO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Service
public class OperationClaimService {

    private final OperationRequestDAO operationDAO;
    private final OperationClaimWriter writer;
    private final ObjectMapper mapper;
    private final DataSource dataSource;

    public OperationClaimService(
            OperationRequestDAO operationDAO,
            OperationClaimWriter writer,
            ObjectMapper mapper,
            DataSource dataSource
    ) {
        this.operationDAO = operationDAO;
        this.writer = writer;
        this.mapper = mapper;
        this.dataSource = dataSource;
    }

    public Claim claim(RequestKey key, Map<String, Object> payload) {
        String canonical = CanonicalPayload.canonicalize(payload);
        String fingerprint = CanonicalPayload.sha256(canonical);

        try {
            UUID operationId = writer.insertProcessing(key, fingerprint, payload);

            return new Claim(operationId, ClaimOutcome.NEW, null);
        } catch (SQLException e) {

            // Check duplicate key
            if (!(e.getErrorCode() == 1062)) {
                throw new IllegalStateException("Claim SQL failed", e);
            }

            try (Connection connection = dataSource.getConnection()) {

                OperationClaimData existing = operationDAO.findByKey(connection, key);

                if (existing == null) {
                    throw new IllegalArgumentException("Duplicate key doesn't match request key");
                }

                if (!fingerprint.equalsIgnoreCase(existing.fingerprint())) {
                    throw new IdempotencyConflictException("IDEMPOTENCY_CONFLICT");
                }

                OperationStatus status = existing.status();

                return switch (status) {
                    case PROCESSING -> new Claim(existing.id(), ClaimOutcome.PROCESSING, null);
                    case SUCCEEDED, FAILED -> {
                        OperationResult result = mapper.treeToValue(existing.reponsePayload(), OperationResult.class);
                        yield new Claim(existing.id(), ClaimOutcome.REPLAY, result);
                    }
                };

            } catch (SQLException lookupError) {
                throw new IllegalArgumentException("Cannot read operation", lookupError);
            }
        }
    }
}

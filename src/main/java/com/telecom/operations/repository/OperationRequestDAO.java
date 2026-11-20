package com.telecom.operations.repository;

import com.telecom.operations.dto.OperationResult;
import com.telecom.operations.entity.OperationRequest;
import com.telecom.operations.model.OperationClaimData;
import com.telecom.operations.model.OperationStatus;
import com.telecom.operations.model.RequestKey;
import com.telecom.operations.service.CanonicalPayload;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.UUID;

@Repository
public class OperationRequestDAO {

    private final ObjectMapper mapper;

    public OperationRequestDAO(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public OperationClaimData findByKey(
            Connection connection,
            RequestKey key
    )  throws SQLException {
        String sql = """
                SELECT * FROM operation_request
                WHERE caller_id = ?
                    AND operation = ?
                    AND request_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, key.callerId());
            ps.setString(2, key.operation().name());
            ps.setString(3, key.requestId());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String responseJson = rs.getString("response_payload");
                JsonNode responsePayload = responseJson == null ? null : mapper.readTree(responseJson);

                OperationClaimData op = new OperationClaimData(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("fingerprint"),
                        OperationStatus.valueOf(rs.getString("status")),
                        responsePayload
                );

                return op;
            }
        }
    }

    public int insertProcessing(
            Connection connection,
            OperationRequest op
    ) throws SQLException {
        String sql = """
            INSERT INTO operation_request (
                id,
                caller_id,
                request_id,
                operation,
                fingerprint,
                canonical_payload,
                status,
                received_at,
                fence,
                attempt_count
            ) VALUES (?,?,?,?,?,CAST(? AS JSON),?,?,0,0)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, op.getId().toString());
            ps.setString(2, op.getCallerId());
            ps.setString(3, op.getRequestId());
            ps.setString(4, op.getOperation());
            ps.setString(5, op.getFingerprint());
            ps.setString(6, CanonicalPayload.canonicalize(op.getCanonicalPayload()));
            ps.setString(7, op.getStatus());
            ps.setTimestamp(8, Timestamp.valueOf(op.getReceivedAt()));

            return ps.executeUpdate();

        }
    }

    public OperationStatus findStatusByIdForUpdate(
            Connection connection,
            UUID operationId
    ) throws SQLException {
        String sql = """
                SELECT status FROM operation_request
                WHERE id = ?
                FOR UPDATE
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, operationId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return OperationStatus.valueOf(rs.getString("status"));
            }
        }
    }

    public int completeOperation(
            Connection connection,
            UUID operationId,
            OperationResult result
    ) throws SQLException {
        String sql = """
                UPDATE operation_request
                SET status = ?,
                    response_payload = CAST(? AS JSON),
                    result_code = ?,
                    completed_at = UTC_TIMESTAMP(6)
                WHERE id = ?
                AND status = 'PROCESSING'
                """;

        OperationStatus status = result.status();

        if (status != OperationStatus.SUCCEEDED && status != OperationStatus.FAILED) {
            throw new IllegalArgumentException("Operation must have a final status");
        }

        String responseJson = mapper.writeValueAsString(result);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, responseJson);
            ps.setString(3, result.code());
            ps.setString(4, operationId.toString());

            return ps.executeUpdate();
        }
    }
}

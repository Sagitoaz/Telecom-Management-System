package com.telecom.operations.service;

import com.telecom.operations.entity.OperationRequest;
import com.telecom.operations.model.RequestKey;
import com.telecom.operations.repository.OperationRequestDAO;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class OperationClaimWriter {
    private final OperationRequestDAO operationDAO;
    private final DataSource dataSource;

    public OperationClaimWriter(OperationRequestDAO operationDAO, DataSource dataSource) {
        this.operationDAO = operationDAO;
        this.dataSource = dataSource;
    }

    public UUID insertProcessing(
            RequestKey key,
            String fingerprint,
            Map<String, Object> payload
    ) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                LocalDateTime receivedAt = getDBTime(connection);

                OperationRequest op = OperationRequest.newRequest(
                        key.callerId(),
                        key.requestId(),
                        key.operation(),
                        fingerprint,
                        payload,
                        receivedAt
                );
                int rows = operationDAO.insertProcessing(connection, op);

                if (rows != 1) {
                    throw new IllegalStateException("Insert to operation request fail");
                }

                connection.commit();

                return op.getId();
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    private LocalDateTime getDBTime(Connection connection) throws SQLException {
        LocalDateTime receivedAt;
        String sql = "SELECT UTC_TIMESTAMP(6)";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            if (!rs.next()) {
                throw new SQLException("Couldn't get database time");
            }

            receivedAt = rs.getObject(1, LocalDateTime.class);

            return receivedAt;
        }
    }
}

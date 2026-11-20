package com.telecom.account.repository;

import com.telecom.account.entity.ServiceAccount;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class ServiceAccountDAO {
    public int updateBalance(
            Connection connection,
            UUID accountId,
            long version,
            long newBalance
    ) throws SQLException {
        String sql = """
                UPDATE service_account
                SET version = version + 1,
                    balance_vnd = ?
                WHERE id = ?
                AND version = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, newBalance);
            ps.setString(2, accountId.toString());
            ps.setLong(3, version);

            return ps.executeUpdate();
        }
    }

    public ServiceAccount findByIdForUpdate(
            Connection connection,
            UUID accountId
    ) throws SQLException {
        String sql = """
                SELECT * FROM service_account
                WHERE id = ?
                FOR UPDATE
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, accountId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                ServiceAccount account = new ServiceAccount(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("subscriber_id")),
                        rs.getString("account_type"),
                        rs.getLong("balance_vnd"),
                        rs.getObject("expires_at", LocalDateTime.class),
                        rs.getString("status"),
                        rs.getLong("version"),
                        rs.getObject("created_at", LocalDateTime.class)
                );

                return account;
            }
        }
    }
}

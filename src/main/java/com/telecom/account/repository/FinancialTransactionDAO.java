package com.telecom.account.repository;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class FinancialTransactionDAO {
    public int insertTransaction(
            Connection connection,
            UUID id,
            UUID operID,
            UUID accountId,
            UUID subscriberId,
            UUID subscriptionId,
            UUID subscriptionCycleId,
            String sourceType,
            String direction,
            long amount,
            long oldBalance,
            long newBalance,
            LocalDateTime eligibilityCheckedAt,
            String logicalKey
    ) throws SQLException {
        String sql = """
                INSERT INTO financial_transaction (
                    id,
                    operation_request_id,
                    account_id,
                    subscriber_id,
                    subscription_id,
                    subscription_cycle_id,
                    source_type,
                    direction,
                    amount_vnd,
                    balance_before,
                    balance_after,
                    recorded_at,
                    eligibility_checked_at,
                    logical_key
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,UTC_TIMESTAMP(6),?,?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, id.toString());
            ps.setString(2, operID.toString());
            ps.setString(3, accountId.toString());
            ps.setString(4, subscriberId.toString());
            ps.setString(5, subscriptionId == null ? null : subscriptionId.toString());
            ps.setString(6, subscriptionCycleId == null ? null : subscriptionCycleId.toString());
            ps.setString(7, sourceType);
            ps.setString(8, direction);
            ps.setLong(9, amount);
            ps.setLong(10, oldBalance);
            ps.setLong(11, newBalance);
            ps.setTimestamp(12, Timestamp.valueOf(eligibilityCheckedAt));
            ps.setString(13, logicalKey);

            return ps.executeUpdate();

        }
    }
}

package com.telecom.operations.repository;

import com.telecom.operations.entity.OperationRequest;
import com.telecom.operations.model.RequestKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface OperationRequestRepository extends JpaRepository<OperationRequest, UUID> {
    @NativeQuery("""
    SELECT * FROM operation_request
    WHERE caller_id = :callerId
        AND operation = :operation
        AND request_id = :requestId
    """)
    Optional<OperationRequest> findByCallerIdAndOperationAndRequestId(
        @Param("callerId") String callerId,
        @Param("operation") String operation,
        @Param("requestId") String requestId
    );

    @Modifying
    @NativeQuery("""
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
        ) VALUES (
                      :id,
                      :callerId,
                      :requestId,
                      :operation,
                      :fingerprint,
                      CAST(:canonicalPayload AS JSON),
                      :status,
                      :receivedAt,
                      0,
                      0
            )
    """)
    int insertProcessing(
        @Param("id") UUID id,
        @Param("callerId") String callerId,
        @Param("requestId") String requestId,
        @Param("operation") String operation,
        @Param("fingerprint") String fingerprint,
        @Param("canonicalPayload") String payload,
        @Param("status") String status,
        @Param("receivedAt")LocalDateTime receivedAt
    );

    default Optional<OperationRequest> findByKey(RequestKey key) {
        return findByCallerIdAndOperationAndRequestId(
            key.callerId(),
            key.operation().name(),
            key.requestId()
        );
    }
}

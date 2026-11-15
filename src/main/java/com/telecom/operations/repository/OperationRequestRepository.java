package com.telecom.operations.repository;

import com.telecom.operations.entity.OperationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OperationRequestRepository extends JpaRepository<OperationRequest, UUID> {
    @NativeQuery("""
    SELECT * FROM operation_request
    WHERE caller_id = :callerId
        AND operation = :operation
        And request_id = :requestId
    """)
    Optional<OperationRequest> findByCallerIdAndOperationAndRequestId(
        @Param("callerId") String callerId,
        @Param("operation") String operation,
        @Param("requestId") String requestId
    );
}

package com.telecom.operations.repository;

import com.telecom.operations.entity.OperationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRequestRepository extends JpaRepository<OperationRequest, String> {
}

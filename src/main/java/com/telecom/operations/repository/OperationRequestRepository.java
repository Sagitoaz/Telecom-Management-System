package com.telecom.operations.repository;

import com.telecom.operations.entity.OperationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OperationRequestRepository extends JpaRepository<OperationRequest, UUID> {

}

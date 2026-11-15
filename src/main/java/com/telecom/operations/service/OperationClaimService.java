package com.telecom.operations.service;

import com.telecom.account.dto.CreditRequest;
import com.telecom.operations.model.Claim;
import com.telecom.operations.repository.OperationRequestRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class OperationClaimService {

    private final OperationRequestRepository operationRepo;

    public OperationClaimService(OperationRequestRepository operationRepo) {
        this.operationRepo = operationRepo;
    }

    @Transactional
    public Claim claim(UUID accountId, CreditRequest request, String callerId) {

        return null;
    }
}

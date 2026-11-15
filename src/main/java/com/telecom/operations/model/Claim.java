package com.telecom.operations.model;

import com.telecom.operations.dto.OperationResult;

import java.util.UUID;

public record Claim(
    UUID operationRequestId,
    ClaimOutcome outcome,
    OperationResult existingResult
) {}
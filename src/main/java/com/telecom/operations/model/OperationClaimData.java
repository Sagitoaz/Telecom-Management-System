package com.telecom.operations.model;

import tools.jackson.databind.JsonNode;

import java.util.UUID;

public record OperationClaimData(
        UUID id,
        String fingerprint,
        OperationStatus status,
        JsonNode reponsePayload
) {}

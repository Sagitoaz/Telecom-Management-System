package com.telecom.operations.model;

public record RequestKey(
        String callerId,
        OperationName operation,
        String requestId
) {}

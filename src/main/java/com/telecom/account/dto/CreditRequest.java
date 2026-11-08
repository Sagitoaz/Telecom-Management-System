package com.telecom.account.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreditRequest(
    @NotBlank @Size(max = 128) String requestId,
    @NotNull UUID subscriberId,
    @NotNull @Positive Long amountVnd
) {}

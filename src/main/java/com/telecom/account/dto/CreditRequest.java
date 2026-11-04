package com.telecom.account.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreditRequest(
    @NotBlank String requestId,
    @NotNull UUID subscriberId,
    @NotNull @Min(1) @Max(10_000_000) Long amountVnd
) {}

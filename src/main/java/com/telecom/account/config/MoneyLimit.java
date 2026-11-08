package com.telecom.account.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "telecom.money")
public record MoneyLimit(
    @Positive long maxBalance,
    @Positive long maxCreditAmount
) {}

package com.telecom.account.dto;

import java.util.UUID;

public record FinancialResult(
        UUID txnId,
        UUID accountId,
        long amountVnd,
        long balanceBefore,
        long balanceAfter
) {}

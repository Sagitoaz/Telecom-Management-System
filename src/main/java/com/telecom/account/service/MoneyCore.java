package com.telecom.account.service;

import com.telecom.account.config.MoneyLimit;
import org.springframework.stereotype.Component;

@Component
public final class MoneyCore {

    private final MoneyLimit limit;

    public MoneyCore(MoneyLimit limit) {
        this.limit = limit;
    }

    public long calculateCredit(long balance, long amount) {
        if (balance < 0 || balance > limit.maxBalance()) {
            throw new IllegalArgumentException("Invalid balance");
        }
        if (amount <= 0 || amount > limit.maxCreditAmount()) {
            throw new IllegalArgumentException("Invalid amount");
        }

        long newBalance = Math.addExact(balance, amount);

        if (newBalance > limit.maxBalance()) {
            throw new IllegalArgumentException("Balance limit exceed");
        }
        return newBalance;
    }
}

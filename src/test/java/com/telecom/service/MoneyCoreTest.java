package com.telecom.service;

import com.telecom.account.config.MoneyLimit;
import com.telecom.account.service.MoneyCore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyCoreTest {
    private final MoneyCore moneyCore = new MoneyCore(new MoneyLimit(100_000_000, 10_000_000));

    @Test
    void creditSuccess() {
        long result = moneyCore.calculateCredit(50_000, 20_000);
        assertEquals(70_000, result);
    }

    @Test
    void creditAtMaxBalance() {
        long result = moneyCore.calculateCredit(90_000_000, 10_000_000);
        assertEquals(100_000_000, result);
    }

    @Test
    void rejectMaxBalance() {
        assertThrows(IllegalArgumentException.class, () -> moneyCore.calculateCredit(200_000_000, 10_000));
    }

    @Test
    void rejectZeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> moneyCore.calculateCredit(0, 0));
    }

    @Test
    void rejectMaxAmount() {
        assertThrows(IllegalArgumentException.class, () -> moneyCore.calculateCredit(20_000, 15_000_000));
    }

    @Test
    void rejectBalanceLimit() {
        assertThrows(IllegalArgumentException.class, () -> moneyCore.calculateCredit(90_000_000, 20_000_000));
    }
}

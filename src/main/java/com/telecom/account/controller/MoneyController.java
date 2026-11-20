package com.telecom.account.controller;

import com.telecom.account.dto.CreditRequest;
import com.telecom.account.service.MoneyService;
import com.telecom.operations.dto.OperationResult;
import com.telecom.security.HeaderIdentityResolver;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class MoneyController {

    private final MoneyService moneyService;
    private final HeaderIdentityResolver callerIdentityResolver;

    public MoneyController(MoneyService moneyService, HeaderIdentityResolver callerIdentityResolver) {
        this.moneyService = moneyService;
        this.callerIdentityResolver = callerIdentityResolver;
    }

    @PostMapping("/{accountId}/credits")
    public OperationResult creditMoney(
            @PathVariable UUID accountId,
            @Valid @RequestBody CreditRequest request
    ) throws SQLException {
        String callerId = callerIdentityResolver.resolve();
        return moneyService.creditMoney(accountId, request, callerId);
    }
}

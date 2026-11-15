package com.telecom.account.controller;

import com.telecom.account.dto.CreditRequest;
import com.telecom.account.service.MoneyService;
import com.telecom.operations.dto.OperationResult;
import com.telecom.security.CallerIdentityResolver;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class MoneyController {

    private final MoneyService moneyService;
    private final CallerIdentityResolver callerIdentityResolver;

    public MoneyController(MoneyService moneyService, CallerIdentityResolver callerIdentityResolver) {
        this.moneyService = moneyService;
        this.callerIdentityResolver = callerIdentityResolver;
    }

    @PostMapping("/{accountId}/credits")
    public OperationResult creditMoney(
            @PathVariable UUID accountId,
            @Valid @RequestBody CreditRequest request,
            @AuthenticationPrincipal Jwt jwt
            ) {
        String callerId = callerIdentityResolver.resolve(jwt);
        return moneyService.creditMoney(accountId, request, callerId);
    }
}

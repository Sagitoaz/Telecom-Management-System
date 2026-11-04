package com.telecom.account.controller;

import com.telecom.account.dto.CreditRequest;
import com.telecom.account.service.MoneyService;
import com.telecom.operations.dto.OperationResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class MoneyController {

    private final MoneyService moneyService;

    public MoneyController(MoneyService moneyService) {
        this.moneyService = moneyService;
    }

    @PostMapping("/{accountId}/credits")
    public OperationResult creditMoney(
            @PathVariable String accountId,
            @Valid @RequestBody CreditRequest request
            ) {
        return moneyService.creditMoney(accountId, request);
    }
}

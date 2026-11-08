package com.telecom.account.service;

import com.telecom.account.dto.CreditRequest;
import com.telecom.account.entity.ServiceAccount;
import com.telecom.account.repository.ServiceAccountRepository;
import com.telecom.operations.dto.OperationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class MoneyService {

    private final ServiceAccountRepository accountRepo;
    private final MoneyCore moneyCore;

    public MoneyService(ServiceAccountRepository accountRepo, MoneyCore moneyCore) {
        this.accountRepo = accountRepo;
        this.moneyCore = moneyCore;
    }

    @Transactional
    public OperationResult creditMoney(UUID accountId, CreditRequest request) {
        Optional<ServiceAccount> optAcc = accountRepo.findByIdForUpdate(accountId);
        ServiceAccount account;

        if (optAcc.isPresent()) {
            account = optAcc.get();
        } else {
            throw new IllegalArgumentException("Account not found");
        }

        long oldBalance = account.getBalanceVnd();
        long newBalance = moneyCore.calculateCredit(account.getBalanceVnd(), request.amountVnd());

        int update = accountRepo.updateBalance(
                account.getId(),
                account.getVersion(),
                newBalance
        );

        if (update != 1) {
            throw new IllegalArgumentException("Update account balance failed");
        }



        return null;
    }
}

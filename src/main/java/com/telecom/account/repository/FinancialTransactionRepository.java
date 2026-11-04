package com.telecom.account.repository;

import com.telecom.account.entity.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, String> {
}

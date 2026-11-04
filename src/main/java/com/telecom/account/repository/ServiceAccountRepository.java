package com.telecom.account.repository;

import com.telecom.account.entity.ServiceAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceAccountRepository extends JpaRepository<ServiceAccount, String> {
}

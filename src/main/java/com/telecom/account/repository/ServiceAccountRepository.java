package com.telecom.account.repository;

import com.telecom.account.entity.ServiceAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ServiceAccountRepository extends JpaRepository<ServiceAccount, UUID> {

    @NativeQuery("""
    SELECT * FROM service_account
        WHERE id = :accountId
        FOR UPDATE 
    """)
    Optional<ServiceAccount> findByIdForUpdate (@Param("accountId") UUID accountId);

    @Modifying
    @NativeQuery("""
    UPDATE service_account
        SET balanceVND = :newBalance
            AND version = version + 1
        WHERE accountId = :accountId
            AND version = :version
    """)
    int updateBalance(
            @Param("accountId") UUID accountId,
            @Param("version") long version,
            @Param("newBalance") long newBalance
    );
}

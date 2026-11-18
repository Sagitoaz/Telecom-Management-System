package com.telecom.operations.service;

import com.telecom.account.dto.CreditRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CanonicalPayloadTest {
    @Test
    void samePayloadProducesSameFingerprint() {
        UUID accountId = UUID.randomUUID();
        UUID subscriberId = UUID.randomUUID();

        CreditRequest request1 = new CreditRequest("REQ-001", subscriberId, 30000L);
        CreditRequest request2 = new CreditRequest("REQ-002", subscriberId, 30000L);

        String hash1 = CanonicalPayload.sha256(
                CanonicalPayload.canonicalize(
                        CanonicalPayload.creditPayload(accountId, request1)
                )
        );

        String hash2 = CanonicalPayload.sha256(
                CanonicalPayload.canonicalize(
                        CanonicalPayload.creditPayload(accountId, request2)
                )
        );

        assertEquals(hash1, hash2);
    }
}

package com.telecom.operations.service;

import com.telecom.account.dto.CreditRequest;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public final class CanonicalPayload {
    private static final ObjectMapper mapper = new ObjectMapper();

    private CanonicalPayload() {};

    public static Map<String, Object> creditPayload(UUID accountId, CreditRequest request) {
        Map<String, Object> data = new TreeMap<>();
        data.put("subscriberId", request.subscriberId().toString());
        data.put("accountId", accountId.toString());
        data.put("amountVnd", request.amountVnd().toString());
        return data;
    }

    public static String canonicalize(Map<String, Object> data) {
        return mapper.writeValueAsString(new TreeMap<>(data));
    }

    public static String sha256(String canonical) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not supported");
        }
    }
}
package com.telecom.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CallerIdentityResolver {
    public String resolve(Jwt jwt) {
        String callerId = jwt.getClaimAsString("client_id");
        if (callerId == null || callerId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing client identity");
        }
        return callerId;
    }
}

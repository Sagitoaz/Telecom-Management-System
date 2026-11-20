package com.telecom.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class HeaderIdentityResolver implements CallerIdentityResolver {

    private final HttpServletRequest request;

    public HeaderIdentityResolver(HttpServletRequest request) {
        this.request = request;
    }
    @Override
    public String resolve() {
        String callerId = request.getHeader("X-Caller-Id");

        if (callerId == null || callerId.isBlank()) {
            throw new IllegalArgumentException("Missing caller id header");
        }

        callerId = callerId.trim();

        if (!callerId.matches("[A-Za-z0-9_-]{1,64}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid X-Caller-Id");
        }

        return callerId;
    }
}

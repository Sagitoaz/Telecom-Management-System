package com.telecom.security;

import org.springframework.security.oauth2.jwt.Jwt;

public interface CallerIdentityResolver {
    String resolve();
}

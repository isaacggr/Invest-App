package com.isaacggr.investapp.dto.auth;

import java.util.UUID;

public record LoginResponse(
        String token,
        String type,
        UUID userId,
        String email,
        long expiresIn
) {
    public LoginResponse(String token, UUID userId, String email, long expiresIn) {
        this(token, "Bearer", userId, email, expiresIn);
    }
}

package com.isaacggr.investapp.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        boolean active
) {
}
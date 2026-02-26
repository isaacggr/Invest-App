package com.isaacggr.investapp.dto.account;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        UUID userId,
        String name,
        boolean active
) {
}
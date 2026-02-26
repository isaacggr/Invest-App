package com.isaacggr.investapp.dto.account;

import java.util.UUID;

public record CreateAccountRequest(
        UUID userId,
        String name
) {
}
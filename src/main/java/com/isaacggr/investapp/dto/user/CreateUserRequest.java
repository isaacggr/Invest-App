package com.isaacggr.investapp.dto.user;

public record CreateUserRequest(
        String name,
        String email,
        String password
) {
}
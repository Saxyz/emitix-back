package com.unimag.emitix.dto;

public record LoginResponse(
        String token,
        String username,
        String fullName,
        String role
) {}

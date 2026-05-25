package com.unimag.emitix.dto;

import java.util.UUID;

public record MeResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        String phone,
        String role,
        boolean isActive
) {}

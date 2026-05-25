package com.unimag.emitix.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        String username,
        String userFullName,
        String action,
        String entityType,
        String entityId,
        String details,
        String ipAddress,
        LocalDateTime createdAt
) {}

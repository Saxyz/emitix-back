package com.unimag.emitix.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String code,
        String name,
        String description,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        String unit,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

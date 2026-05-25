package com.unimag.emitix.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResolutionResponse(
        UUID id,
        String prefix,
        long fromNumber,
        long toNumber,
        long currentNumber,
        LocalDate validFrom,
        LocalDate validUntil,
        boolean isActive,
        LocalDateTime createdAt
) {}

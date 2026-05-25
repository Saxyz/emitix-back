package com.unimag.emitix.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @Size(max = 20, message = "El código UNSPSC no puede superar 20 caracteres")
        String code,

        @NotBlank(message = "El nombre del producto es requerido")
        @Size(max = 255)
        String name,

        @Size(max = 500)
        String description,

        @NotNull(message = "El precio unitario es requerido")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal unitPrice,

        @NotNull(message = "La tasa de impuesto es requerida")
        @DecimalMin(value = "0.0", message = "La tasa de impuesto no puede ser negativa")
        @DecimalMax(value = "100.0", message = "La tasa de impuesto no puede superar 100%")
        BigDecimal taxRate,

        @Size(max = 20)
        String unit
) {}

package com.unimag.emitix.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record InvoiceItemRequest(
        @NotBlank(message = "La descripción del ítem es requerida")
        String description,

        @NotNull(message = "La cantidad es requerida")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a 0")
        BigDecimal quantity,

        @NotNull(message = "El precio unitario es requerido")
        @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo")
        BigDecimal unitPrice,

        @DecimalMin(value = "0.00", message = "La tasa de impuesto no puede ser negativa")
        @DecimalMax(value = "1.00", message = "La tasa de impuesto no puede superar el 100%")
        BigDecimal taxRate
) {}

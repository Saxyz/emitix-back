package com.unimag.emitix.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceItemResponse(
        UUID id,
        String description,
        String unspscCode,
        String unit,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal discount,
        String taxType,
        BigDecimal taxRate,
        BigDecimal subtotal
) {}

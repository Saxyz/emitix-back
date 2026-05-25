package com.unimag.emitix.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        String number,
        String prefix,
        String status,
        String invoiceType,
        BuyerResponse buyer,
        CompanyResponse company,
        BigDecimal subtotal,
        BigDecimal taxTotal,
        BigDecimal total,
        String pdfUrl,
        String xmlUrl,
        String createdBy,
        LocalDateTime issuedAt,
        List<InvoiceItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

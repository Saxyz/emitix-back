package com.unimag.emitix.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull(message = "El ID del cliente es requerido")
        UUID buyerId,

        @Pattern(regexp = "sale|credit_note|debit_note", message = "invoiceType debe ser sale, credit_note o debit_note")
        String invoiceType,

        @Pattern(regexp = "CASH|TRANSFER|CARD|CREDIT", message = "paymentMethod debe ser CASH, TRANSFER, CARD o CREDIT")
        String paymentMethod,

        LocalDate dueDate,

        String notes,

        @NotEmpty(message = "La factura debe tener al menos un ítem")
        @Valid
        List<InvoiceItemRequest> items
) {}

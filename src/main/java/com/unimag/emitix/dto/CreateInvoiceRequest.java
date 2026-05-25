package com.unimag.emitix.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull(message = "El ID del cliente es requerido")
        UUID BuyerId,

        String notes,

        @NotEmpty(message = "La factura debe tener al menos un ítem")
        @Valid
        List<InvoiceItemRequest> items
) {}

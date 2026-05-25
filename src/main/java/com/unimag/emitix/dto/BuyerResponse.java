package com.unimag.emitix.dto;

import java.util.UUID;

public record BuyerResponse(
        UUID id,
        String nit,
        String fullName,
        String email,
        String address,
        String documentType
) {}

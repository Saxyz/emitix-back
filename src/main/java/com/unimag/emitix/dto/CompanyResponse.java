package com.unimag.emitix.dto;

import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String nit,
        String legalName,
        String address,
        String city,
        String logoUrl
) {}

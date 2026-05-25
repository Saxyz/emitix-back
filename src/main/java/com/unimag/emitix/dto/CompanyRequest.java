package com.unimag.emitix.dto;

import jakarta.validation.constraints.*;

public record CompanyRequest(
        @NotBlank(message = "El NIT es requerido")
        @Size(max = 20, message = "El NIT no puede superar 20 caracteres")
        String nit,

        @NotBlank(message = "El nombre legal de la empresa es requerido")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String legalName,

        @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
        String address,

        @Size(max = 100, message = "La ciudad no puede superar 100 caracteres")
        String city,

        String logoUrl
) {}

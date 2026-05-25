package com.unimag.emitix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BuyerRequest(
        @NotBlank(message = "El NIT/documento es requerido")
        @Size(max = 20)
        String nit,

        @NotBlank(message = "El tipo de documento es requerido")
        @Pattern(regexp = "NIT|CC|CE|PASAPORTE", message = "Tipo de documento inválido")
        String documentType,

        @NotBlank(message = "El nombre completo es requerido")
        @Size(max = 255)
        String fullName,

        @Email(message = "Email inválido")
        String email,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address
) {}

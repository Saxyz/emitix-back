package com.unimag.emitix.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ResolutionRequest(
        @NotBlank(message = "El prefijo es requerido")
        @Size(max = 10)
        String prefix,

        @NotNull(message = "El número inicial es requerido")
        @Min(value = 1, message = "El número inicial debe ser mayor a 0")
        Long fromNumber,

        @NotNull(message = "El número final es requerido")
        @Min(value = 1, message = "El número final debe ser mayor a 0")
        Long toNumber,

        @NotNull(message = "La fecha de inicio de vigencia es requerida")
        LocalDate validFrom,

        @NotNull(message = "La fecha de fin de vigencia es requerida")
        LocalDate validUntil
) {}

package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoPlatoRequest(
        @NotNull @Positive Long idPlato,
        @Min(1) int cantidad) {
}

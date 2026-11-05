package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearPedidoRequest(
        @NotNull @Positive Long idRestaurante,
        @NotEmpty List<@NotNull @Valid PedidoPlatoRequest> platos) {
}

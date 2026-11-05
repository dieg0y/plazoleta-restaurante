package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.time.Instant;

import com.plazoleta.restaurante.domain.model.EstadoPedido;

public record HistorialEstadoPedidoResponse(
        Long id,
        EstadoPedido estado,
        Instant fechaCambio) {
}

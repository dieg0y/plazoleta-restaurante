package com.plazoleta.restaurante.domain.model;

import java.time.Instant;

public record HistorialEstadoPedido(
        Long id,
        Long idPedido,
        EstadoPedido estado,
        Instant fechaCambio) {
}

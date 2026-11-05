package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.plazoleta.restaurante.domain.model.EstadoPedido;

public record PedidoResponse(
        Long id,
        Long idCliente,
        Long idRestaurante,
        EstadoPedido estado,
        Long idEmpleadoAsignado,
        BigDecimal valorTotal,
        Instant fechaCreacion,
        Instant fechaFinalizacion,
        List<PedidoPlatoResponse> platos) {

    public PedidoResponse {
        platos = List.copyOf(platos);
    }
}

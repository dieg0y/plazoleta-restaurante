package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.math.BigDecimal;

public record DuracionPedidoResponse(
        Long idPedido,
        Long idEmpleado,
        BigDecimal duracionSegundos) {
}

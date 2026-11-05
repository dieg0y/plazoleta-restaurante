package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.math.BigDecimal;

public record PedidoPlatoResponse(
        Long idPlato,
        String nombrePlato,
        BigDecimal precioUnitario,
        int cantidad) {
}

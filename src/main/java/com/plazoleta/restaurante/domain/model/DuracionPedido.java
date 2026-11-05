package com.plazoleta.restaurante.domain.model;

import java.math.BigDecimal;

public record DuracionPedido(
        Long idPedido,
        Long idEmpleado,
        BigDecimal duracionSegundos) {
}

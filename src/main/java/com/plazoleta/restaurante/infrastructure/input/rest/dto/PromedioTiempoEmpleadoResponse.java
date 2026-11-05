package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.math.BigDecimal;

public record PromedioTiempoEmpleadoResponse(
        Long idEmpleado,
        long pedidosEntregados,
        BigDecimal promedioDuracionSegundos,
        int posicion) {
}

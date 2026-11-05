package com.plazoleta.restaurante.domain.model;

import java.math.BigDecimal;

public record PromedioTiempoEmpleado(
        Long idEmpleado,
        long pedidosEntregados,
        BigDecimal promedioDuracionSegundos,
        int posicion) {
}

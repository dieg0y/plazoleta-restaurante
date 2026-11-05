package com.plazoleta.restaurante.domain.model;

import java.math.BigDecimal;

public record PedidoPlato(Long idPlato, String nombrePlato, BigDecimal precioUnitario, int cantidad) {
}

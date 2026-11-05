package com.plazoleta.restaurante.domain.model;

import java.util.List;

public record InformeEficienciaPedidos(
        List<DuracionPedido> pedidos,
        List<PromedioTiempoEmpleado> ranking) {

    public InformeEficienciaPedidos {
        pedidos = List.copyOf(pedidos);
        ranking = List.copyOf(ranking);
    }
}

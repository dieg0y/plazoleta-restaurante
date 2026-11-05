package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.util.List;

public record EficienciaPedidosResponse(
        List<DuracionPedidoResponse> pedidos,
        List<PromedioTiempoEmpleadoResponse> rankingEmpleados) {

    public EficienciaPedidosResponse {
        pedidos = List.copyOf(pedidos);
        rankingEmpleados = List.copyOf(rankingEmpleados);
    }
}

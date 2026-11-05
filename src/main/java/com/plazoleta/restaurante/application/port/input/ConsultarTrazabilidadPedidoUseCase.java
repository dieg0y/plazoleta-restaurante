package com.plazoleta.restaurante.application.port.input;

import java.util.List;

import com.plazoleta.restaurante.domain.model.HistorialEstadoPedido;

public interface ConsultarTrazabilidadPedidoUseCase {

    List<HistorialEstadoPedido> consultar(Long idPedido, Long idCliente);
}

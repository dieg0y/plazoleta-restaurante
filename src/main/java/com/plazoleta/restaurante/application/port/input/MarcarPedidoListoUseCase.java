package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Pedido;

public interface MarcarPedidoListoUseCase {

    Pedido marcarListo(Long idPedido, Long idUsuarioEmpleado);
}

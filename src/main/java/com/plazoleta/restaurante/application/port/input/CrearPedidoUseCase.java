package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Pedido;

public interface CrearPedidoUseCase {

    Pedido crear(Long idCliente, CrearPedidoCommand command);
}

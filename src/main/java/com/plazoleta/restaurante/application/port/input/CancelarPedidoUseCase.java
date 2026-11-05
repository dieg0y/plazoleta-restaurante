package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Pedido;

public interface CancelarPedidoUseCase {

    Pedido cancelar(Long idPedido, Long idCliente);
}

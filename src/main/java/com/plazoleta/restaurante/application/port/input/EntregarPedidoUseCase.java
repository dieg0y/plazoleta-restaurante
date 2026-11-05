package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Pedido;

public interface EntregarPedidoUseCase {

    Pedido entregar(Long idPedido, Long idUsuarioEmpleado, String pin);
}

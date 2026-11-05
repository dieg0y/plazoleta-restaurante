package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Pedido;

public interface AsignarPedidoUseCase {

    Pedido asignar(Long idPedido, Long idUsuarioEmpleado);
}

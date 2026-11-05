package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;

public interface ListarPedidosUseCase {

    Pagina<Pedido> listar(Long idUsuarioEmpleado, EstadoPedido estado, int pagina, int tamano);
}

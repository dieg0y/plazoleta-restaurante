package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;

public interface ListarPedidosClienteUseCase {

    Pagina<Pedido> listar(Long idCliente, Long idPedido, int pagina, int tamano);
}

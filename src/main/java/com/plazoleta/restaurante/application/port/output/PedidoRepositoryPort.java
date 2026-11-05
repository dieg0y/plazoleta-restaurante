package com.plazoleta.restaurante.application.port.output;

import java.util.Optional;

import com.plazoleta.restaurante.domain.model.Pedido;

public interface PedidoRepositoryPort {

    Pedido guardar(Pedido pedido);

    boolean existePedidoActivoPorCliente(Long idCliente);

    Optional<Pedido> obtenerPorId(Long idPedido);
}

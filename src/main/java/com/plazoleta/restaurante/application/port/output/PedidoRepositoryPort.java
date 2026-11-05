package com.plazoleta.restaurante.application.port.output;

import java.util.Optional;
import java.util.List;

import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.HistorialEstadoPedido;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;

public interface PedidoRepositoryPort {

    Pedido guardar(Pedido pedido);

    boolean existePedidoActivoPorCliente(Long idCliente);

    Optional<Pedido> obtenerPorId(Long idPedido);

    Pagina<Pedido> listarPorRestauranteYEstado(
            Long idRestaurante, EstadoPedido estado, int pagina, int tamano);

    List<HistorialEstadoPedido> listarHistorialEstados(Long idPedido);

    List<Pedido> listarEntregadosPorRestaurante(Long idRestaurante);
}

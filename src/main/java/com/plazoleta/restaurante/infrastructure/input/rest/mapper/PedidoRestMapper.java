package com.plazoleta.restaurante.infrastructure.input.rest.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.domain.model.Pedido;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PedidoPlatoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PedidoResponse;

@Component
public class PedidoRestMapper {

    public PedidoResponse toResponse(Pedido pedido) {
        List<PedidoPlatoResponse> platos = pedido.getPlatos().stream()
                .map(plato -> new PedidoPlatoResponse(
                        plato.idPlato(),
                        plato.nombrePlato(),
                        plato.precioUnitario(),
                        plato.cantidad()))
                .toList();
        return new PedidoResponse(
                pedido.getId(),
                pedido.getIdCliente(),
                pedido.getIdRestaurante(),
                pedido.getEstado(),
                pedido.getIdEmpleadoAsignado(),
                pedido.getValorTotal(),
                pedido.getFechaCreacion(),
                pedido.getFechaFinalizacion(),
                platos);
    }
}

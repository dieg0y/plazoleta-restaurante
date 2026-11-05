package com.plazoleta.restaurante.application.service;

import org.springframework.transaction.annotation.Transactional;

import com.plazoleta.restaurante.application.port.input.CancelarPedidoUseCase;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelarPedidoService implements CancelarPedidoUseCase {

    private static final String MENSAJE_NO_CANCELABLE =
            "Lo sentimos, tu pedido ya está en preparación y no puede cancelarse";

    private final PedidoRepositoryPort pedidoRepositoryPort;

    @Override
    @Transactional
    public Pedido cancelar(Long idPedido, Long idCliente) {
        Pedido pedido = pedidoRepositoryPort.obtenerPorId(idPedido)
                .orElseThrow(() -> new BusinessException("El pedido no existe"));

        if (!idCliente.equals(pedido.getIdCliente())) {
            throw new BusinessException("No puedes cancelar un pedido que no te pertenece");
        }
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException(MENSAJE_NO_CANCELABLE);
        }

        return pedidoRepositoryPort.guardar(pedido.toBuilder()
                .estado(EstadoPedido.CANCELADO)
                .build());
    }
}

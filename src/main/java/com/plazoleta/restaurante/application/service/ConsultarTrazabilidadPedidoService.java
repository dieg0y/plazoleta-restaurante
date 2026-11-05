package com.plazoleta.restaurante.application.service;

import java.util.List;

import com.plazoleta.restaurante.application.port.input.ConsultarTrazabilidadPedidoUseCase;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.HistorialEstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarTrazabilidadPedidoService implements ConsultarTrazabilidadPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;

    @Override
    public List<HistorialEstadoPedido> consultar(Long idPedido, Long idCliente) {
        Pedido pedido = pedidoRepositoryPort.obtenerPorId(idPedido)
                .orElseThrow(() -> new BusinessException("El pedido no existe"));
        if (!idCliente.equals(pedido.getIdCliente())) {
            throw new BusinessException("No puedes consultar la trazabilidad de un pedido que no te pertenece");
        }
        return pedidoRepositoryPort.listarHistorialEstados(idPedido);
    }
}

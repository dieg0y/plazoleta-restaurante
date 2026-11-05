package com.plazoleta.restaurante.application.service;

import java.time.Instant;

import org.springframework.transaction.annotation.Transactional;

import com.plazoleta.restaurante.application.port.input.EntregarPedidoUseCase;
import com.plazoleta.restaurante.application.port.output.CodigoSeguridadPedidoPort;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntregarPedidoService implements EntregarPedidoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final CodigoSeguridadPedidoPort codigoSeguridadPedidoPort;

    @Override
    @Transactional
    public Pedido entregar(Long idPedido, Long idUsuarioEmpleado, String pin) {
        var empleado = empleadoRepositoryPort.obtenerPorIdUsuario(idUsuarioEmpleado)
                .orElseThrow(() -> new BusinessException("El empleado no está asociado a un restaurante"));
        Pedido pedido = pedidoRepositoryPort.obtenerPorId(idPedido)
                .orElseThrow(() -> new BusinessException("El pedido no existe"));

        if (!empleado.getIdRestaurante().equals(pedido.getIdRestaurante())) {
            throw new BusinessException("El pedido no pertenece al restaurante del empleado");
        }
        if (pedido.getEstado() != EstadoPedido.LISTO) {
            throw new BusinessException("Solo se pueden entregar pedidos que estén listos");
        }
        if (!codigoSeguridadPedidoPort.coincide(pin, pedido.getCodigoPinHash())) {
            throw new BusinessException("El PIN de seguridad es incorrecto");
        }

        return pedidoRepositoryPort.guardar(pedido.toBuilder()
                .estado(EstadoPedido.ENTREGADO)
                .fechaFinalizacion(Instant.now())
                .build());
    }
}

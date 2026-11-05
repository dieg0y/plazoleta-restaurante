package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.AsignarPedidoUseCase;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AsignarPedidoService implements AsignarPedidoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;

    @Override
    @Transactional
    public Pedido asignar(Long idPedido, Long idUsuarioEmpleado) {
        var empleado = empleadoRepositoryPort.obtenerPorIdUsuario(idUsuarioEmpleado)
                .orElseThrow(() -> new BusinessException("El empleado no está asociado a un restaurante"));
        Pedido pedido = pedidoRepositoryPort.obtenerPorId(idPedido)
                .orElseThrow(() -> new BusinessException("El pedido no existe"));

        if (!empleado.getIdRestaurante().equals(pedido.getIdRestaurante())) {
            throw new BusinessException("El pedido no pertenece al restaurante del empleado");
        }
        if (pedido.getEstado() != EstadoPedido.PENDIENTE || pedido.getIdEmpleadoAsignado() != null) {
            throw new BusinessException("Solo se pueden asignar pedidos pendientes y sin asignar");
        }

        return pedidoRepositoryPort.guardar(pedido.toBuilder()
                .estado(EstadoPedido.EN_PREPARACION)
                .idEmpleadoAsignado(empleado.getIdUsuario())
                .build());
    }
}

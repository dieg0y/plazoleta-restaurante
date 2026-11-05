package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.MarcarPedidoListoUseCase;
import com.plazoleta.restaurante.application.port.output.CodigoSeguridadPedidoPort;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.EnviarSmsPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class MarcarPedidoListoService implements MarcarPedidoListoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final UsuarioClientePort usuarioClientePort;
    private final CodigoSeguridadPedidoPort codigoSeguridadPedidoPort;
    private final EnviarSmsPort enviarSmsPort;

    @Override
    @Transactional
    public Pedido marcarListo(Long idPedido, Long idUsuarioEmpleado) {
        var empleado = empleadoRepositoryPort.obtenerPorIdUsuario(idUsuarioEmpleado)
                .orElseThrow(() -> new BusinessException("El empleado no está asociado a un restaurante"));
        Pedido pedido = pedidoRepositoryPort.obtenerPorId(idPedido)
                .orElseThrow(() -> new BusinessException("El pedido no existe"));

        if (!empleado.getIdRestaurante().equals(pedido.getIdRestaurante())) {
            throw new BusinessException("El pedido no pertenece al restaurante del empleado");
        }
        if (pedido.getEstado() != EstadoPedido.EN_PREPARACION
                || !idUsuarioEmpleado.equals(pedido.getIdEmpleadoAsignado())) {
            throw new BusinessException("Solo el empleado asignado puede marcar como listo un pedido en preparación");
        }

        String celular = usuarioClientePort.obtenerCelularCliente(pedido.getIdCliente());
        String codigo = codigoSeguridadPedidoPort.generar();
        String codigoCodificado = codigoSeguridadPedidoPort.codificar(codigo);
        enviarSmsPort.enviar(
                celular,
                "Tu pedido " + pedido.getId() + " está listo. PIN de seguridad: " + codigo);

        return pedidoRepositoryPort.guardar(pedido.toBuilder()
                .estado(EstadoPedido.LISTO)
                .codigoPinHash(codigoCodificado)
                .build());
    }
}

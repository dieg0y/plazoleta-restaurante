package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.output.CodigoSeguridadPedidoPort;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.EnviarSmsPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

@ExtendWith(MockitoExtension.class)
class MarcarPedidoListoServiceTest {

    @Mock
    private EmpleadoRepositoryPort empleadoRepositoryPort;

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private UsuarioClientePort usuarioClientePort;

    @Mock
    private CodigoSeguridadPedidoPort codigoSeguridadPedidoPort;

    @Mock
    private EnviarSmsPort enviarSmsPort;

    @InjectMocks
    private MarcarPedidoListoService service;

    @Test
    void notificaPorSmsYGuardaElHashAlMarcarListo() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(4L, EstadoPedido.EN_PREPARACION, 22L)));
        when(usuarioClientePort.obtenerCelularCliente(14L)).thenReturn("+573001234567");
        when(codigoSeguridadPedidoPort.generar()).thenReturn("123456");
        when(codigoSeguridadPedidoPort.codificar("123456")).thenReturn("hash-seguro");
        when(pedidoRepositoryPort.guardar(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = service.marcarListo(9L, 22L);

        assertEquals(EstadoPedido.LISTO, resultado.getEstado());
        assertEquals("hash-seguro", resultado.getCodigoPinHash());
        verify(enviarSmsPort).enviar("+573001234567", "Tu pedido 9 está listo. PIN de seguridad: 123456");
    }

    @Test
    void noNotificaNiCambiaUnPedidoDeOtroRestaurante() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(5L, EstadoPedido.EN_PREPARACION, 22L)));

        assertThrows(BusinessException.class, () -> service.marcarListo(9L, 22L));

        verify(usuarioClientePort, never()).obtenerCelularCliente(any());
        verify(enviarSmsPort, never()).enviar(any(), any());
        verify(pedidoRepositoryPort, never()).guardar(any());
    }

    @Test
    void requiereQueElEmpleadoAsignadoProceseUnPedidoEnPreparacion() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(4L, EstadoPedido.PENDIENTE, null)));

        assertThrows(BusinessException.class, () -> service.marcarListo(9L, 22L));

        verify(enviarSmsPort, never()).enviar(any(), any());
        verify(pedidoRepositoryPort, never()).guardar(any());
    }

    private Pedido pedido(Long idRestaurante, EstadoPedido estado, Long idEmpleado) {
        return Pedido.builder()
                .id(9L)
                .idCliente(14L)
                .idRestaurante(idRestaurante)
                .estado(estado)
                .idEmpleadoAsignado(idEmpleado)
                .build();
    }
}

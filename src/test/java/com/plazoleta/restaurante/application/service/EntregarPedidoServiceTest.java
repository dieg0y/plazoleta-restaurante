package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

@ExtendWith(MockitoExtension.class)
class EntregarPedidoServiceTest {

    @Mock
    private EmpleadoRepositoryPort empleadoRepositoryPort;

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private CodigoSeguridadPedidoPort codigoSeguridadPedidoPort;

    @InjectMocks
    private EntregarPedidoService service;

    @Test
    void entregaPedidoListoSoloConPinValido() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(4L, EstadoPedido.LISTO, "hash-seguro")));
        when(codigoSeguridadPedidoPort.coincide("123456", "hash-seguro")).thenReturn(true);
        when(pedidoRepositoryPort.guardar(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = service.entregar(9L, 22L, "123456");

        assertEquals(EstadoPedido.ENTREGADO, resultado.getEstado());
        assertNotNull(resultado.getFechaFinalizacion());
        verify(pedidoRepositoryPort).guardar(any(Pedido.class));
    }

    @Test
    void rechazaPinIncorrectoSinCambiarElPedido() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(4L, EstadoPedido.LISTO, "hash-seguro")));
        when(codigoSeguridadPedidoPort.coincide("000000", "hash-seguro")).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.entregar(9L, 22L, "000000"));

        verify(pedidoRepositoryPort, never()).guardar(any(Pedido.class));
    }

    @Test
    void noPermiteEntregarUnPedidoQueNoEstaListo() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(4L, EstadoPedido.EN_PREPARACION, "hash-seguro")));

        assertThrows(BusinessException.class, () -> service.entregar(9L, 22L, "123456"));

        verify(codigoSeguridadPedidoPort, never()).coincide(any(), any());
        verify(pedidoRepositoryPort, never()).guardar(any(Pedido.class));
    }

    @Test
    void noPermiteEntregarUnPedidoDeOtroRestaurante() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(5L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(4L, EstadoPedido.LISTO, "hash-seguro")));

        assertThrows(BusinessException.class, () -> service.entregar(9L, 22L, "123456"));

        verify(codigoSeguridadPedidoPort, never()).coincide(any(), any());
        verify(pedidoRepositoryPort, never()).guardar(any(Pedido.class));
    }

    private Pedido pedido(Long idRestaurante, EstadoPedido estado, String codigoPinHash) {
        return Pedido.builder()
                .id(9L)
                .idCliente(14L)
                .idRestaurante(idRestaurante)
                .estado(estado)
                .codigoPinHash(codigoPinHash)
                .build();
    }
}

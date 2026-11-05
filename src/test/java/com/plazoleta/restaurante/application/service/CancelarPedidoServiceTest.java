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

import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

@ExtendWith(MockitoExtension.class)
class CancelarPedidoServiceTest {

    private static final String MENSAJE_NO_CANCELABLE =
            "Lo sentimos, tu pedido ya está en preparación y no puede cancelarse";

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private CancelarPedidoService service;

    @Test
    void cancelaPedidoPendienteDelClienteAutenticado() {
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(14L, EstadoPedido.PENDIENTE)));
        when(pedidoRepositoryPort.guardar(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = service.cancelar(9L, 14L);

        assertEquals(EstadoPedido.CANCELADO, resultado.getEstado());
        verify(pedidoRepositoryPort).guardar(any(Pedido.class));
    }

    @Test
    void noPermiteCancelarPedidoDeOtroCliente() {
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(15L, EstadoPedido.PENDIENTE)));

        assertThrows(BusinessException.class, () -> service.cancelar(9L, 14L));

        verify(pedidoRepositoryPort, never()).guardar(any(Pedido.class));
    }

    @Test
    void devuelveElMensajeExactoCuandoElPedidoYaNoEstaPendiente() {
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(14L, EstadoPedido.EN_PREPARACION)));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> service.cancelar(9L, 14L));

        assertEquals(MENSAJE_NO_CANCELABLE, exception.getMessage());
        verify(pedidoRepositoryPort, never()).guardar(any(Pedido.class));
    }

    private Pedido pedido(Long idCliente, EstadoPedido estado) {
        return Pedido.builder()
                .id(9L)
                .idCliente(idCliente)
                .idRestaurante(4L)
                .estado(estado)
                .build();
    }
}

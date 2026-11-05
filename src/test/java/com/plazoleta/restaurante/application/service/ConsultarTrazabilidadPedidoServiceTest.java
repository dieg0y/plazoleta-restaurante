package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.HistorialEstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

@ExtendWith(MockitoExtension.class)
class ConsultarTrazabilidadPedidoServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private ConsultarTrazabilidadPedidoService service;

    @Test
    void devuelveLaTrazabilidadSoloAlClientePropietario() {
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(Pedido.builder().id(9L).idCliente(14L).build()));
        List<HistorialEstadoPedido> esperado = List.of(
                new HistorialEstadoPedido(1L, 9L, EstadoPedido.PENDIENTE, Instant.parse("2026-11-05T15:00:00Z")));
        when(pedidoRepositoryPort.listarHistorialEstados(9L)).thenReturn(esperado);

        assertEquals(esperado, service.consultar(9L, 14L));
    }

    @Test
    void noExponeElHistorialDeOtroCliente() {
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(Pedido.builder().id(9L).idCliente(15L).build()));

        assertThrows(BusinessException.class, () -> service.consultar(9L, 14L));

        verify(pedidoRepositoryPort, never()).listarHistorialEstados(9L);
    }
}

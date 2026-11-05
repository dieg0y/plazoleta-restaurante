package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;

@ExtendWith(MockitoExtension.class)
class ListarPedidosServiceTest {

    @Mock
    private EmpleadoRepositoryPort empleadoRepositoryPort;

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private ListarPedidosService service;

    @Test
    void filtraPorEstadoYRestringeLosResultadosAlRestauranteDelEmpleado() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        Pedido pedido = Pedido.builder().id(1L).idRestaurante(4L).estado(EstadoPedido.PENDIENTE).build();
        when(pedidoRepositoryPort.listarPorRestauranteYEstado(4L, EstadoPedido.PENDIENTE, 1, 5))
                .thenReturn(new Pagina<>(List.of(pedido), 1, 5, 6));

        Pagina<Pedido> resultado = service.listar(22L, EstadoPedido.PENDIENTE, 1, 5);

        assertEquals(1, resultado.contenido().size());
        assertEquals(6, resultado.totalElementos());
        verify(pedidoRepositoryPort).listarPorRestauranteYEstado(4L, EstadoPedido.PENDIENTE, 1, 5);
    }

    @Test
    void rechazaEmpleadoSinRestauranteAsociado() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.listar(22L, EstadoPedido.PENDIENTE, 0, 10));

        verify(pedidoRepositoryPort, never()).listarPorRestauranteYEstado(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), anyInt(), anyInt());
    }

    @Test
    void rechazaEstadoYPaginacionInvalidos() {
        assertThrows(BusinessException.class, () -> service.listar(22L, null, 0, 10));
        assertThrows(BusinessException.class, () -> service.listar(22L, EstadoPedido.PENDIENTE, -1, 10));
        assertThrows(BusinessException.class, () -> service.listar(22L, EstadoPedido.PENDIENTE, 0, 0));
    }
}

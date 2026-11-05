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

import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;

@ExtendWith(MockitoExtension.class)
class AsignarPedidoServiceTest {

    @Mock
    private EmpleadoRepositoryPort empleadoRepositoryPort;

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private AsignarPedidoService service;

    @Test
    void asignaAlEmpleadoAutenticadoYTransicionaAPreparacion() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L)).thenReturn(Optional.of(pedido(4L, EstadoPedido.PENDIENTE, null)));
        when(pedidoRepositoryPort.guardar(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = service.asignar(9L, 22L);

        assertEquals(EstadoPedido.EN_PREPARACION, resultado.getEstado());
        assertEquals(22L, resultado.getIdEmpleadoAsignado());
        verify(pedidoRepositoryPort).guardar(any(Pedido.class));
    }

    @Test
    void impideAsignarPedidosDeOtroRestaurante() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L)).thenReturn(Optional.of(pedido(5L, EstadoPedido.PENDIENTE, null)));

        assertThrows(BusinessException.class, () -> service.asignar(9L, 22L));

        verify(pedidoRepositoryPort, never()).guardar(any(Pedido.class));
    }

    @Test
    void soloPermiteAsignarPedidosPendientesSinAsignar() {
        when(empleadoRepositoryPort.obtenerPorIdUsuario(22L))
                .thenReturn(Optional.of(Empleado.builder().idUsuario(22L).idRestaurante(4L).build()));
        when(pedidoRepositoryPort.obtenerPorId(9L))
                .thenReturn(Optional.of(pedido(4L, EstadoPedido.EN_PREPARACION, 21L)));

        assertThrows(BusinessException.class, () -> service.asignar(9L, 22L));

        verify(pedidoRepositoryPort, never()).guardar(any(Pedido.class));
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

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
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Pedido;
import com.plazoleta.restaurante.domain.model.Restaurante;

@ExtendWith(MockitoExtension.class)
class ConsultarEficienciaPedidosServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @InjectMocks
    private ConsultarEficienciaPedidosService service;

    @Test
    void calculaDuracionPorPedidoYOrdenaElRankingPorPromedioAscendente() {
        when(restauranteRepositoryPort.obtenerPorId(4L))
                .thenReturn(Optional.of(Restaurante.builder().id(4L).idPropietario(100L).build()));
        when(pedidoRepositoryPort.listarEntregadosPorRestaurante(4L)).thenReturn(List.of(
                pedido(1L, 20L, "2026-11-05T15:00:00Z", "2026-11-05T15:00:50Z"),
                pedido(2L, 10L, "2026-11-05T15:00:00Z", "2026-11-05T15:01:40.500Z"),
                pedido(3L, 10L, "2026-11-05T15:00:00Z", "2026-11-05T15:05:00Z")));

        var informe = service.consultar(4L, 100L);

        assertEquals(List.of("50.000", "100.500", "300.000"),
                informe.pedidos().stream().map(p -> p.duracionSegundos().toPlainString()).toList());
        assertEquals(List.of(20L, 10L), informe.ranking().stream().map(p -> p.idEmpleado()).toList());
        assertEquals(List.of("50.00", "200.25"),
                informe.ranking().stream().map(p -> p.promedioDuracionSegundos().toPlainString()).toList());
        assertEquals(List.of(1, 2), informe.ranking().stream().map(p -> p.posicion()).toList());
    }

    @Test
    void restringeLaConsultaAlPropietarioDelRestaurante() {
        when(restauranteRepositoryPort.obtenerPorId(4L))
                .thenReturn(Optional.of(Restaurante.builder().id(4L).idPropietario(101L).build()));

        assertThrows(BusinessException.class, () -> service.consultar(4L, 100L));

        verify(pedidoRepositoryPort, never()).listarEntregadosPorRestaurante(4L);
    }

    private Pedido pedido(Long id, Long idEmpleado, String inicio, String fin) {
        return Pedido.builder()
                .id(id)
                .idRestaurante(4L)
                .estado(com.plazoleta.restaurante.domain.model.EstadoPedido.ENTREGADO)
                .idEmpleadoAsignado(idEmpleado)
                .fechaCreacion(Instant.parse(inicio))
                .fechaFinalizacion(Instant.parse(fin))
                .build();
    }
}

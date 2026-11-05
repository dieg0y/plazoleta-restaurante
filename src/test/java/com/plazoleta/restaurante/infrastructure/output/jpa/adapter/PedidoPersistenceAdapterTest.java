package com.plazoleta.restaurante.infrastructure.output.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.HistorialEstadoPedido;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;
import com.plazoleta.restaurante.domain.model.PedidoPlato;

@SpringBootTest
@Transactional
class PedidoPersistenceAdapterTest {

    @Autowired
    private PedidoPersistenceAdapter adapter;

    @Test
    void persistePedidoYCapturaLosPlatosYSusPrecios() {
        Pedido creado = adapter.guardar(Pedido.builder()
                .idCliente(41L)
                .idRestaurante(51L)
                .estado(EstadoPedido.PENDIENTE)
                .valorTotal(new BigDecimal("24000"))
                .fechaCreacion(Instant.parse("2026-11-05T15:00:00Z"))
                .platos(List.of(new PedidoPlato(61L, "Plato al precio original", new BigDecimal("12000"), 2)))
                .build());

        Pedido leido = adapter.obtenerPorId(creado.getId()).orElseThrow();

        assertThat(leido.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(leido.getValorTotal()).isEqualByComparingTo("24000");
        assertThat(leido.getPlatos()).hasSize(1);
        assertThat(leido.getPlatos().get(0).idPlato()).isEqualTo(61L);
        assertThat(leido.getPlatos().get(0).nombrePlato()).isEqualTo("Plato al precio original");
        assertThat(leido.getPlatos().get(0).precioUnitario()).isEqualByComparingTo("12000");
        assertThat(leido.getPlatos().get(0).cantidad()).isEqualTo(2);
        assertThat(adapter.existePedidoActivoPorCliente(41L)).isTrue();
        assertThat(adapter.listarHistorialEstados(creado.getId()))
                .extracting(HistorialEstadoPedido::estado)
                .containsExactly(EstadoPedido.PENDIENTE);
    }

    @Test
    void listaSoloLosPedidosDelRestauranteYEstadoSolicitadosConPaginacion() {
        Pedido primero = guardarPedido(71L, EstadoPedido.PENDIENTE, "2026-11-05T15:00:00Z");
        guardarPedido(71L, EstadoPedido.LISTO, "2026-11-05T15:01:00Z");
        guardarPedido(72L, EstadoPedido.PENDIENTE, "2026-11-05T15:02:00Z");
        Pedido segundo = guardarPedido(71L, EstadoPedido.PENDIENTE, "2026-11-05T15:03:00Z");

        Pagina<Pedido> primera = adapter.listarPorRestauranteYEstado(71L, EstadoPedido.PENDIENTE, 0, 1);
        Pagina<Pedido> segundaPagina = adapter.listarPorRestauranteYEstado(71L, EstadoPedido.PENDIENTE, 1, 1);

        assertThat(primera.contenido()).extracting(Pedido::getId).containsExactly(primero.getId());
        assertThat(segundaPagina.contenido()).extracting(Pedido::getId).containsExactly(segundo.getId());
        assertThat(primera.totalElementos()).isEqualTo(2);
        assertThat(primera.totalPaginas()).isEqualTo(2);
    }

    @Test
    void actualizaEstadoYEmpleadoSinPerderLosPlatosDelPedido() {
        Pedido creado = adapter.guardar(Pedido.builder()
                .idCliente(42L)
                .idRestaurante(73L)
                .estado(EstadoPedido.PENDIENTE)
                .valorTotal(new BigDecimal("9000"))
                .fechaCreacion(Instant.parse("2026-11-05T16:00:00Z"))
                .platos(List.of(new PedidoPlato(62L, "Arepa", new BigDecimal("9000"), 1)))
                .build());

        adapter.guardar(creado.toBuilder()
                .estado(EstadoPedido.EN_PREPARACION)
                .idEmpleadoAsignado(23L)
                .build());
        Pedido actualizado = adapter.obtenerPorId(creado.getId()).orElseThrow();

        assertThat(actualizado.getEstado()).isEqualTo(EstadoPedido.EN_PREPARACION);
        assertThat(actualizado.getIdEmpleadoAsignado()).isEqualTo(23L);
        assertThat(actualizado.getPlatos()).hasSize(1);
        assertThat(actualizado.getPlatos().get(0).nombrePlato()).isEqualTo("Arepa");
        assertThat(adapter.listarHistorialEstados(creado.getId()))
                .extracting(HistorialEstadoPedido::estado)
                .containsExactly(EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION);
    }

    private Pedido guardarPedido(Long idRestaurante, EstadoPedido estado, String fecha) {
        return adapter.guardar(Pedido.builder()
                .idCliente(41L)
                .idRestaurante(idRestaurante)
                .estado(estado)
                .valorTotal(BigDecimal.ZERO)
                .fechaCreacion(Instant.parse(fecha))
                .platos(List.of())
                .build());
    }
}

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
    }
}

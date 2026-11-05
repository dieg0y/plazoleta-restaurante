package com.plazoleta.restaurante.infrastructure.output.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PlatoEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.RestauranteEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.PlatoJpaRepository;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.RestauranteJpaRepository;

@SpringBootTest
@Transactional
class PaginacionPersistenceAdapterTest {

    @Autowired
    private RestaurantePersistenceAdapter restauranteAdapter;

    @Autowired
    private PlatoPersistenceAdapter platoAdapter;

    @Autowired
    private RestauranteJpaRepository restauranteRepository;

    @Autowired
    private PlatoJpaRepository platoRepository;

    @Test
    void paginaRestaurantesOrdenaYAplicaDesplazamientoSinPageable() {
        long totalInicial = restauranteRepository.count();
        restauranteRepository.saveAllAndFlush(List.of(
                restaurante("ZZZ restaurante paginacion"),
                restaurante("ZZZ restaurante paginacion final")));

        Pagina<Restaurante> pagina = restauranteAdapter.listarOrdenadosPorNombre((int) totalInicial, 1);

        assertThat(pagina.contenido()).hasSize(1);
        assertThat(pagina.contenido().get(0).getNombre()).isEqualTo("ZZZ restaurante paginacion");
        assertThat(pagina.totalElementos()).isEqualTo(totalInicial + 2);
    }

    @Test
    void paginaPlatosAplicaFiltroCategoriaYDisponibilidad() {
        RestauranteEntity restaurante = restauranteRepository.saveAndFlush(restaurante("Restaurante de prueba"));
        platoRepository.saveAllAndFlush(List.of(
                plato(restaurante.getId(), "A plato activo", true),
                plato(restaurante.getId(), "B plato activo", true),
                plato(restaurante.getId(), "C plato inactivo", false)));

        Pagina<com.plazoleta.restaurante.domain.model.Plato> primeraPagina =
                platoAdapter.listarPorRestaurante(restaurante.getId(), CategoriaPlato.PLATO_FUERTE, 0, 1);
        Pagina<com.plazoleta.restaurante.domain.model.Plato> segundaPagina =
                platoAdapter.listarPorRestaurante(restaurante.getId(), CategoriaPlato.PLATO_FUERTE, 1, 1);

        assertThat(primeraPagina.contenido()).extracting("nombre").containsExactly("A plato activo");
        assertThat(segundaPagina.contenido()).extracting("nombre").containsExactly("B plato activo");
        assertThat(primeraPagina.totalElementos()).isEqualTo(2);
        assertThat(primeraPagina.totalPaginas()).isEqualTo(2);
    }

    private RestauranteEntity restaurante(String nombre) {
        return RestauranteEntity.builder()
                .nombre(nombre)
                .direccion("Calle de prueba")
                .telefono("+573001234567")
                .urlLogo("https://example.com/logo.png")
                .nit("900123456")
                .idPropietario(1L)
                .build();
    }

    private PlatoEntity plato(Long idRestaurante, String nombre, boolean activo) {
        return PlatoEntity.builder()
                .nombre(nombre)
                .categoria(CategoriaPlato.PLATO_FUERTE)
                .descripcion("Plato para probar paginación")
                .precio(new BigDecimal("10000"))
                .urlImagen("https://example.com/plato.png")
                .idRestaurante(idRestaurante)
                .activo(activo)
                .build();
    }
}

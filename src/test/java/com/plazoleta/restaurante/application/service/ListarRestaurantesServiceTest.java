package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Restaurante;

@ExtendWith(MockitoExtension.class)
class ListarRestaurantesServiceTest {

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @InjectMocks
    private ListarRestaurantesService listarRestaurantesService;

    @Test
    @DisplayName("Lista restaurantes delegando en el repositorio con la paginación pedida")
    void debeListarRestaurantes() {
        List<Restaurante> restaurantes = List.of(
                Restaurante.builder().id(1L).nombre("Fogon de Pandi")
                        .urlLogo("https://logo.com/fogon.png").build(),
                Restaurante.builder().id(2L).nombre("La Parrilla")
                        .urlLogo("https://logo.com/parrilla.png").build());
        Pagina<Restaurante> pagina = new Pagina<>(restaurantes, 0, 10, 2);
        when(restauranteRepositoryPort.listarOrdenadosPorNombre(0, 10)).thenReturn(pagina);

        Pagina<Restaurante> resultado = listarRestaurantesService.listarRestaurantes(0, 10);

        assertEquals(2, resultado.contenido().size());
        assertEquals(2, resultado.totalElementos());
        assertEquals(1, resultado.totalPaginas());
        assertEquals("Fogon de Pandi", resultado.contenido().get(0).getNombre());
    }

    @Test
    @DisplayName("Rechaza un número de página negativo")
    void debeRechazarPaginaNegativa() {
        assertThrows(BusinessException.class,
                () -> listarRestaurantesService.listarRestaurantes(-1, 10));

        verify(restauranteRepositoryPort, never()).listarOrdenadosPorNombre(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rechaza un tamaño de página menor o igual a 0")
    void debeRechazarTamanoInvalido() {
        assertThrows(BusinessException.class,
                () -> listarRestaurantesService.listarRestaurantes(0, 0));

        assertThrows(BusinessException.class,
                () -> listarRestaurantesService.listarRestaurantes(0, -5));

        verify(restauranteRepositoryPort, never()).listarOrdenadosPorNombre(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rechaza un desplazamiento de página que no cabe en la consulta")
    void debeRechazarDesplazamientoDemasiadoGrande() {
        assertThrows(BusinessException.class,
                () -> listarRestaurantesService.listarRestaurantes(Integer.MAX_VALUE, 2));
        verify(restauranteRepositoryPort, never()).listarOrdenadosPorNombre(anyInt(), anyInt());
    }
}
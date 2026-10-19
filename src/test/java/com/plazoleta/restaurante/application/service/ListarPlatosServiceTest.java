package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;

@ExtendWith(MockitoExtension.class)
class ListarPlatosServiceTest {

    @Mock
    private PlatoRepositoryPort platoRepositoryPort;

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @InjectMocks
    private ListarPlatosService listarPlatosService;

    private Plato pasta;
    private Plato sopa;

    @BeforeEach
    void setUp() {
        pasta = Plato.builder()
                .id(1L)
                .nombre("Pasta carbonara")
                .categoria(CategoriaPlato.PLATO_FUERTE)
                .precio(new BigDecimal("25000"))
                .descripcion("Pasta con salsa cremosa")
                .urlImagen("http://imagen.com/pasta.jpg")
                .idRestaurante(1L)
                .activo(true)
                .build();

        sopa = Plato.builder()
                .id(2L)
                .nombre("Sopa de maiz")
                .categoria(CategoriaPlato.ENTRADA)
                .precio(new BigDecimal("12000"))
                .descripcion("Sopa tipica")
                .urlImagen("http://imagen.com/sopa.jpg")
                .idRestaurante(1L)
                .activo(true)
                .build();
    }

    private Restaurante restaurante() {
        return Restaurante.builder()
                .id(1L)
                .idPropietario(5L)
                .build();
    }

    @Test
    @DisplayName("Lista los platos activos paginados, sin filtro de categoría")
    void debeListarPlatosSinCategoria() {
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restaurante()));
        when(platoRepositoryPort.listarPorRestaurante(1L, null, 0, 10))
                .thenReturn(new PaginaPlatos(List.of(pasta, sopa), 0, 10, 2));

        PaginaPlatos resultado = listarPlatosService.listarPlatos(1L, null, 0, 10);

        assertEquals(2, resultado.getContenido().size());
        assertEquals(2, resultado.getTotalElementos());
        assertEquals(1, resultado.getTotalPaginas());
    }

    @Test
    @DisplayName("Filtra por categoría cuando se pide")
    void debeListarPlatosConCategoria() {
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restaurante()));
        when(platoRepositoryPort.listarPorRestaurante(1L, CategoriaPlato.PLATO_FUERTE, 0, 10))
                .thenReturn(new PaginaPlatos(List.of(pasta), 0, 10, 1));

        PaginaPlatos resultado = listarPlatosService.listarPlatos(1L, CategoriaPlato.PLATO_FUERTE, 0, 10);

        assertEquals(1, resultado.getContenido().size());
        assertEquals("Pasta carbonara", resultado.getContenido().get(0).getNombre());
        verify(platoRepositoryPort).listarPorRestaurante(1L, CategoriaPlato.PLATO_FUERTE, 0, 10);
    }

    @Test
    @DisplayName("El propietario puede consultar también los platos inactivos de su restaurante")
    void debePermitirAlPropietarioListarPlatosInactivos() {
        Plato deshabilitado = Plato.builder()
                .id(3L)
                .nombre("Postre deshabilitado")
                .categoria(CategoriaPlato.POSTRE)
                .precio(new BigDecimal("9000"))
                .descripcion("Temporalmente no disponible")
                .urlImagen("http://imagen.com/postre.jpg")
                .idRestaurante(1L)
                .activo(false)
                .build();
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restaurante()));
        when(platoRepositoryPort.listarTodosPorRestaurante(1L, null, 0, 10))
                .thenReturn(new PaginaPlatos(List.of(pasta, deshabilitado), 0, 10, 2));

        PaginaPlatos resultado = listarPlatosService.listarPlatos(1L, null, 0, 10, true, 5L);

        assertEquals(2, resultado.getTotalElementos());
        verify(platoRepositoryPort).listarTodosPorRestaurante(1L, null, 0, 10);
        verify(platoRepositoryPort, never()).listarPorRestaurante(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rechaza que un propietario consulte platos inactivos de otro restaurante")
    void debeRechazarPropietarioAjenoAlConsultarPlatosInactivos() {
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restaurante()));

        assertThrows(BusinessException.class,
                () -> listarPlatosService.listarPlatos(1L, null, 0, 10, true, 9L));

        verify(platoRepositoryPort, never()).listarTodosPorRestaurante(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Calcula bien el total de páginas cuando hay más platos que el tamaño")
    void debeCalcularTotalPaginas() {
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restaurante()));
        when(platoRepositoryPort.listarPorRestaurante(1L, null, 0, 10))
                .thenReturn(new PaginaPlatos(List.of(pasta), 0, 10, 25));

        PaginaPlatos resultado = listarPlatosService.listarPlatos(1L, null, 0, 10);

        assertEquals(25, resultado.getTotalElementos());
        assertEquals(3, resultado.getTotalPaginas()); // 25 platos / 10 por página
    }

    @Test
    @DisplayName("Rechaza si el restaurante no existe")
    void debeRechazarRestauranteInexistente() {
        when(restauranteRepositoryPort.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> listarPlatosService.listarPlatos(99L, null, 0, 10));

        verify(platoRepositoryPort, never()).listarPorRestaurante(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rechaza un número de página negativo")
    void debeRechazarPaginaNegativa() {
        assertThrows(BusinessException.class,
                () -> listarPlatosService.listarPlatos(1L, null, -1, 10));

        verify(platoRepositoryPort, never()).listarPorRestaurante(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Rechaza un tamaño de página menor o igual a 0")
    void debeRechazarTamanoInvalido() {
        assertThrows(BusinessException.class,
                () -> listarPlatosService.listarPlatos(1L, null, 0, 0));

        verify(platoRepositoryPort, never()).listarPorRestaurante(any(), any(), anyInt(), anyInt());
    }
}
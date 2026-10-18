package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;

@ExtendWith(MockitoExtension.class)
class ModificarPlatoServiceTest {

    @Mock
    private PlatoRepositoryPort platoRepositoryPort;

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @Mock
    private ValidarPropietarioPort validarPropietarioPort;

    @InjectMocks
    private ModificarPlatoService modificarPlatoService;

    private Plato pasta;

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
    }

    private Restaurante restauranteDueno(Long idPropietario) {
        return Restaurante.builder()
                .id(1L)
                .idPropietario(idPropietario)
                .build();
    }

    @Test
    @DisplayName("Modifica precio y descripción cuando el propietario es el dueño del restaurante")
    void debeModificarPrecioYDescripcionSiendoDueno() {
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        when(platoRepositoryPort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = modificarPlatoService.modificar(
                1L, new BigDecimal("27000"), "Pasta receta mejorada", 5L);

        assertEquals(new BigDecimal("27000"), resultado.getPrecio());
        assertEquals("Pasta receta mejorada", resultado.getDescripcion());
    }

    @Test
    @DisplayName("NO modifica nada aparte de precio y descripción")
    void noDebeModificarOtrosCampos() {
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        when(platoRepositoryPort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = modificarPlatoService.modificar(
                1L, new BigDecimal("27000"), "Pasta receta mejorada", 5L);

        // Todo lo demás queda INTACTO (regla del enunciado)
        assertEquals("Pasta carbonara", resultado.getNombre());
        assertEquals(CategoriaPlato.PLATO_FUERTE, resultado.getCategoria());
        assertEquals(1L, resultado.getIdRestaurante());
    }

    @Test
    @DisplayName("Rechaza si el plato no existe")
    void debeRechazarPlatoInexistente() {
        when(platoRepositoryPort.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> modificarPlatoService.modificar(99L, new BigDecimal("27000"), "desc", 5L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }

    @Test
    @DisplayName("Rechaza si el propietario no es el dueño del restaurante del plato")
    void debeRechazarPropietarioNoDueno() {
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));

        assertThrows(BusinessException.class,
                () -> modificarPlatoService.modificar(1L, new BigDecimal("27000"), "desc", 7L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }

    @Test
    @DisplayName("Rechaza si quien llama no tiene rol propietario")
    void debeRechazarNoPropietario() {
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        doThrow(new BusinessException("El usuario no tiene rol de propietario"))
                .when(validarPropietarioPort).validarPropietario(5L);

        assertThrows(BusinessException.class,
                () -> modificarPlatoService.modificar(1L, new BigDecimal("27000"), "desc", 5L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }

    @Test
    @DisplayName("Rechaza precio 0, negativo o con decimales")
    void debeRechazarPrecioInvalido() {
        assertThrows(BusinessException.class,
                () -> modificarPlatoService.modificar(1L, BigDecimal.ZERO, "desc", 5L));

        assertThrows(BusinessException.class,
                () -> modificarPlatoService.modificar(1L, new BigDecimal("-5000"), "desc", 5L));

        assertThrows(BusinessException.class,
                () -> modificarPlatoService.modificar(1L, new BigDecimal("25000.50"), "desc", 5L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }

    @Test
    @DisplayName("Rechaza una descripción vacía")
    void debeRechazarDescripcionVacia() {
        assertThrows(BusinessException.class,
                () -> modificarPlatoService.modificar(1L, new BigDecimal("27000"), "  ", 5L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }
}
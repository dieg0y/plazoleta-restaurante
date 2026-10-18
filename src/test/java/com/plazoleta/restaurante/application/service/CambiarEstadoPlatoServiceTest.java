package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class CambiarEstadoPlatoServiceTest {

    @Mock
    private PlatoRepositoryPort platoRepositoryPort;

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @Mock
    private ValidarPropietarioPort validarPropietarioPort;

    @InjectMocks
    private CambiarEstadoPlatoService cambiarEstadoPlatoService;

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
    @DisplayName("Deshabilita un plato cuando el propietario es el dueño del restaurante")
    void debeDeshabilitarPlatoSiendoDueno() {
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        when(platoRepositoryPort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = cambiarEstadoPlatoService.cambiarEstado(1L, false, 5L);

        assertFalse(resultado.getActivo());
        verify(platoRepositoryPort).guardar(any(Plato.class));
    }

    @Test
    @DisplayName("Habilita un plato que estaba deshabilitado")
    void debeHabilitarPlatoDeshabilitado() {
        pasta.setActivo(false);
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        when(platoRepositoryPort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = cambiarEstadoPlatoService.cambiarEstado(1L, true, 5L);

        assertTrue(resultado.getActivo());
    }

    @Test
    @DisplayName("NO modifica ningún campo aparte del estado")
    void noDebeModificarOtrosCampos() {
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        when(platoRepositoryPort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = cambiarEstadoPlatoService.cambiarEstado(1L, false, 5L);

        // Todo lo demás queda INTACTO (regla del enunciado)
        assertEquals("Pasta carbonara", resultado.getNombre());
        assertEquals(CategoriaPlato.PLATO_FUERTE, resultado.getCategoria());
        assertEquals(new BigDecimal("25000"), resultado.getPrecio());
        assertEquals("Pasta con salsa cremosa", resultado.getDescripcion());
        assertEquals(1L, resultado.getIdRestaurante());
    }

    @Test
    @DisplayName("Rechaza si el plato no existe")
    void debeRechazarPlatoInexistente() {
        when(platoRepositoryPort.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> cambiarEstadoPlatoService.cambiarEstado(99L, false, 5L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }

    @Test
    @DisplayName("Rechaza si el propietario no es el dueño del restaurante del plato")
    void debeRechazarPropietarioNoDueno() {
        when(platoRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(pasta));
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));

        assertThrows(BusinessException.class,
                () -> cambiarEstadoPlatoService.cambiarEstado(1L, false, 7L));

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
                () -> cambiarEstadoPlatoService.cambiarEstado(1L, false, 5L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }

    @Test
    @DisplayName("Rechaza si el estado no viene en la petición (null)")
    void debeRechazarEstadoNulo() {
        assertThrows(BusinessException.class,
                () -> cambiarEstadoPlatoService.cambiarEstado(1L, null, 5L));

        verify(platoRepositoryPort, never()).guardar(any(Plato.class));
    }
}
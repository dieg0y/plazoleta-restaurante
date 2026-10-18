package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearPlatoServiceTest {

    @Mock
    private PlatoRepositoryPort platoRepositoryPort;

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @Mock
    private ValidarPropietarioPort validarPropietarioPort;

    @InjectMocks
    private CrearPlatoService crearPlatoService;

    private Plato.PlatoBuilder platoValido() {
        return Plato.builder()
                .nombre("Sopa de mondongo")
                .categoria(CategoriaPlato.ENTRADA)
                .descripcion("Sopa tradicional")
                .precio(new BigDecimal("15000"))
                .urlImagen("https://ejemplo.com/mondongo.png")
                .idRestaurante(1L);
    }

    private Restaurante restauranteDePropietario(Long idPropietario) {
        return Restaurante.builder()
                .id(1L)
                .nombre("Sabor Casero")
                .idPropietario(idPropietario)
                .build();
    }

    @Test
    void crearDebeGuardarYDevolverPlatoCuandoTodoEsValido() {
        Plato plato = platoValido().build();
        Plato guardado = platoValido().id(10L).build();
        when(restauranteRepositoryPort.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDePropietario(1L)));
        when(platoRepositoryPort.guardar(plato)).thenReturn(guardado);

        Plato resultado = crearPlatoService.crear(plato, 1L);

        assertEquals(10L, resultado.getId());
        verify(validarPropietarioPort).validarPropietario(1L);
        verify(platoRepositoryPort).guardar(plato);
    }

    @Test
    void crearDebeFallarCuandoElNombreEsNulo() {
        Plato invalido = platoValido().nombre(null).build();

        assertThrows(BusinessException.class, () -> crearPlatoService.crear(invalido, 1L));
        verifyNoInteractions(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Test
    void crearDebeFallarCuandoLaCategoriaEsNula() {
        Plato invalido = platoValido().categoria(null).build();

        assertThrows(BusinessException.class, () -> crearPlatoService.crear(invalido, 1L));
        verifyNoInteractions(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Test
    void crearDebeFallarCuandoElPrecioNoEsPositivo() {
        Plato invalido = platoValido().precio(new BigDecimal("0")).build();

        assertThrows(BusinessException.class, () -> crearPlatoService.crear(invalido, 1L));
        verifyNoInteractions(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Test
    void crearDebeFallarCuandoElIdRestauranteNoEsPositivo() {
        Plato invalido = platoValido().idRestaurante(0L).build();

        assertThrows(BusinessException.class, () -> crearPlatoService.crear(invalido, 1L));
        verifyNoInteractions(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Test
    void crearDebeFallarCuandoElIdPropietarioNoEsPositivo() {
        Plato plato = platoValido().build();

        assertThrows(BusinessException.class, () -> crearPlatoService.crear(plato, 0L));
        verifyNoInteractions(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Test
    void crearDebeFallarCuandoElRestauranteNoExiste() {
        Plato plato = platoValido().idRestaurante(99L).build();
        when(restauranteRepositoryPort.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> crearPlatoService.crear(plato, 1L));
        verify(validarPropietarioPort, never()).validarPropietario(any());
        verify(platoRepositoryPort, never()).guardar(any());
    }

    @Test
    void crearDebeFallarCuandoElPropietarioNoEsElDuenoDelRestaurante() {
        Plato plato = platoValido().build();
        when(restauranteRepositoryPort.obtenerPorId(1L))
                .thenReturn(Optional.of(restauranteDePropietario(2L)));

        assertThrows(BusinessException.class, () -> crearPlatoService.crear(plato, 1L));
        verify(validarPropietarioPort, never()).validarPropietario(any());
        verify(platoRepositoryPort, never()).guardar(any());
    }
}
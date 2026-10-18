package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Restaurante;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearRestauranteServiceTest {

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @Mock
    private ValidarPropietarioPort validarPropietarioPort;

    @InjectMocks
    private CrearRestauranteService crearRestauranteService;

    private Restaurante.RestauranteBuilder builderValido() {
        return Restaurante.builder()
                .nombre("Sabor Casero")
                .direccion("Calle 123 #45-67")
                .telefono("+57300123456")
                .urlLogo("https://ejemplo.com/logo.png")
                .nit("900123456")
                .idPropietario(1L);
    }

    @Test
    void crearDebeGuardarYDevolverRestauranteCuandoLosDatosSonValidos() {
        Restaurante restaurante = builderValido().build();
        Restaurante guardado = builderValido().id(1L).build();
        when(restauranteRepositoryPort.guardar(any(Restaurante.class))).thenReturn(guardado);

        Restaurante resultado = crearRestauranteService.crear(restaurante);

        assertEquals(1L, resultado.getId());
        verify(validarPropietarioPort).validarPropietario(1L);
        verify(restauranteRepositoryPort).guardar(restaurante);
    }

    @Test
    void crearDebeFallarCuandoElNombreEsNulo() {
        Restaurante invalido = builderValido().nombre(null).build();

        assertThrows(BusinessException.class, () -> crearRestauranteService.crear(invalido));
        verifyNoInteractions(validarPropietarioPort, restauranteRepositoryPort);
    }

    @Test
    void crearDebeFallarCuandoElTelefonoTieneCaracteresInvalidos() {
        Restaurante invalido = builderValido().telefono("300 123 4567").build();

        assertThrows(BusinessException.class, () -> crearRestauranteService.crear(invalido));
        verifyNoInteractions(validarPropietarioPort, restauranteRepositoryPort);
    }

    @Test
    void crearDebeFallarCuandoElNitNoEsNumerico() {
        Restaurante invalido = builderValido().nit("900-999-999").build();

        assertThrows(BusinessException.class, () -> crearRestauranteService.crear(invalido));
        verifyNoInteractions(validarPropietarioPort, restauranteRepositoryPort);
    }

    @Test
    void crearDebeFallarCuandoElIdPropietarioNoEsPositivo() {
        Restaurante invalido = builderValido().idPropietario(0L).build();

        assertThrows(BusinessException.class, () -> crearRestauranteService.crear(invalido));
        verifyNoInteractions(validarPropietarioPort, restauranteRepositoryPort);
    }

    @Test
    void crearDebeFallarCuandoElPropietarioNoExiste() {
        Restaurante restaurante = builderValido().idPropietario(999L).build();
        doThrow(new BusinessException("El usuario con id 999 no existe"))
                .when(validarPropietarioPort).validarPropietario(999L);

        assertThrows(BusinessException.class, () -> crearRestauranteService.crear(restaurante));
        verify(restauranteRepositoryPort, never()).guardar(any());
    }

    @Test
    void crearDebeFallarCuandoElPropietarioNoTieneRolPropietario() {
        Restaurante restaurante = builderValido().idPropietario(2L).build();
        doThrow(new BusinessException("El usuario con id 2 no tiene el rol de propietario"))
                .when(validarPropietarioPort).validarPropietario(2L);

        assertThrows(BusinessException.class, () -> crearRestauranteService.crear(restaurante));
        verify(restauranteRepositoryPort, never()).guardar(any());
    }

    @Test
    void crearDebeFallarCuandoElNombreEsSoloNumeros() {
        Restaurante invalido = builderValido().nombre("123456").build();

        assertThrows(BusinessException.class, () -> crearRestauranteService.crear(invalido));
        verifyNoInteractions(validarPropietarioPort, restauranteRepositoryPort);
    }
}
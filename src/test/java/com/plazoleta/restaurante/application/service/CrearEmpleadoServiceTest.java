package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.Restaurante;

@ExtendWith(MockitoExtension.class)
class CrearEmpleadoServiceTest {

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @Mock
    private ValidarPropietarioPort validarPropietarioPort;

    @Mock
    private UsuarioClientePort usuarioClientePort;

    @Mock
    private EmpleadoRepositoryPort empleadoRepositoryPort;

    @InjectMocks
    private CrearEmpleadoService crearEmpleadoService;

    private Empleado empleadoValido;

    @BeforeEach
    void setUp() {
        empleadoValido = Empleado.builder()
                .idRestaurante(1L)
                .nombre("Ana")
                .apellido("Gomez")
                .documentoIdentidad("987654321")
                .celular("+573009876543")
                .fechaNacimiento(LocalDate.of(1998, 3, 10))
                .correo("ana.empleado@correo.com")
                .clave("claveSegura123")
                .build();
    }

    /**
     * Si tu Restaurante necesita más campos para construirse (nit, direccion...),
     * copiá cómo lo armás en tu CrearRestauranteServiceTest y agregalos aquí.
     */
    private Restaurante restauranteDueno(Long idPropietario) {
        return Restaurante.builder()
                .id(1L)
                .idPropietario(idPropietario)
                .build();
    }

    @Test
    @DisplayName("Registra un empleado cuando el propietario es el dueño del restaurante")
    void debeCrearEmpleadoSiendoDueno() {
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        // validarPropietario es void: si no se stubbea, "no hace nada" = pasa la validación
        when(usuarioClientePort.crearCuentaEmpleado(any(Empleado.class))).thenReturn(10L);
        when(empleadoRepositoryPort.guardar(any(Empleado.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Empleado resultado = crearEmpleadoService.crearEmpleado(empleadoValido, 5L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdRestaurante());
    }

    @Test
    @DisplayName("Rechaza si el restaurante no existe")
    void debeRechazarRestauranteInexistente() {
        empleadoValido.setIdRestaurante(99L);
        when(restauranteRepositoryPort.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> crearEmpleadoService.crearEmpleado(empleadoValido, 5L));

        verify(usuarioClientePort, never()).crearCuentaEmpleado(any(Empleado.class));
        verify(empleadoRepositoryPort, never()).guardar(any(Empleado.class));
    }

    @Test
    @DisplayName("Rechaza si quien llama no tiene rol propietario")
    void debeRechazarNoPropietario() {
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        doThrow(new BusinessException("El usuario no tiene rol de propietario"))
                .when(validarPropietarioPort).validarPropietario(5L);

        assertThrows(BusinessException.class,
                () -> crearEmpleadoService.crearEmpleado(empleadoValido, 5L));

        verify(usuarioClientePort, never()).crearCuentaEmpleado(any(Empleado.class));
    }

    @Test
    @DisplayName("Rechaza si el propietario no es el dueño del restaurante")
    void debeRechazarPropietarioNoDueno() {
        when(restauranteRepositoryPort.obtenerPorId(1L)).thenReturn(Optional.of(restauranteDueno(5L)));
        // idPropietario 7L es propietario, pero NO el dueño de este restaurante

        assertThrows(BusinessException.class,
                () -> crearEmpleadoService.crearEmpleado(empleadoValido, 7L));

        verify(usuarioClientePort, never()).crearCuentaEmpleado(any(Empleado.class));
        verify(empleadoRepositoryPort, never()).guardar(any(Empleado.class));
    }
}
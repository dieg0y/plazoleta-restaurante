package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Empleado;

/**
 * PUERTO DE ENTRADA HU4: registrar un empleado en un restaurante.
 * Valida que quien llama sea el propietario dueño del restaurante.
 */
public interface CrearEmpleadoUseCase {

    Empleado crearEmpleado(Empleado empleado, Long idPropietario);
}
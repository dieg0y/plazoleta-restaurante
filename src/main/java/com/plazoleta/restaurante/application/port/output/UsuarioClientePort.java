package com.plazoleta.restaurante.application.port.output;

import com.plazoleta.restaurante.domain.model.Empleado;

/**
 * PUERTO DE SALIDA: comunicación con el microservicio usuario
 * para crear la cuenta del empleado (rol EMPLEADO allá).
 */
public interface UsuarioClientePort {

    /** Crea la cuenta en el microservicio usuario y devuelve el id generado. */
    Long crearCuentaEmpleado(Empleado empleado);
}
package com.plazoleta.restaurante.application.port.output;

import com.plazoleta.restaurante.domain.model.Empleado;

/**
 * PUERTO DE SALIDA: persistencia de la asociación empleado-restaurante.
 */
public interface EmpleadoRepositoryPort {

    Empleado guardar(Empleado empleado);
}
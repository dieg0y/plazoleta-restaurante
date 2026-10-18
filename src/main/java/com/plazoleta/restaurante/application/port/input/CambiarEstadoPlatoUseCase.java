package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * HU7: habilitar o deshabilitar un plato de su restaurante.
 * Solo el propietario dueno del restaurante del plato puede cambiarlo.
 */
public interface CambiarEstadoPlatoUseCase {

    Plato cambiarEstado(Long id, Boolean activo, Long idPropietario);
}
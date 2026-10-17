package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Plato;

public interface CrearPlatoUseCase {

    // TODO HU5: idPropietario saldra del token JWT, no del request
    Plato crear(Plato plato, Long idPropietario);
}
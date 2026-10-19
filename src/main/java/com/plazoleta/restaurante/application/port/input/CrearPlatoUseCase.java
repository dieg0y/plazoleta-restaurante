package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Plato;

public interface CrearPlatoUseCase {

    Plato crear(Plato plato, Long idPropietario);
}
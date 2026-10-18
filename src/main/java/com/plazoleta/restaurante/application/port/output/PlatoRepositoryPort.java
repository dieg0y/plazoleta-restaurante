package com.plazoleta.restaurante.application.port.output;

import com.plazoleta.restaurante.domain.model.Plato;

public interface PlatoRepositoryPort {

    Plato guardar(Plato plato);
}
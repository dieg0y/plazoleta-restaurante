package com.plazoleta.restaurante.application.port.output;

import com.plazoleta.restaurante.domain.model.Restaurante;

public interface RestauranteRepositoryPort {

    Restaurante guardar(Restaurante restaurante);
}
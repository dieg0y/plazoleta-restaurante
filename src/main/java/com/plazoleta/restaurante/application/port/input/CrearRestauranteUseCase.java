package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Restaurante;

public interface CrearRestauranteUseCase {

    Restaurante crear(Restaurante restaurante);
}
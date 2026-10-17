package com.plazoleta.restaurante.application.port.output;

import com.plazoleta.restaurante.domain.model.Restaurante;

import java.util.Optional;

public interface RestauranteRepositoryPort {

    Restaurante guardar(Restaurante restaurante);

    Optional<Restaurante> obtenerPorId(Long id);
}
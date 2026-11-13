package com.plazoleta.restaurante.application.service;

import java.util.List;

import com.plazoleta.restaurante.application.port.input.ListarRestaurantesPropietarioUseCase;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.model.Restaurante;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarRestaurantesPropietarioService implements ListarRestaurantesPropietarioUseCase {

    private final RestauranteRepositoryPort restauranteRepositoryPort;

    @Override
    public List<Restaurante> listarRestaurantes(Long idPropietario) {
        return restauranteRepositoryPort.listarPorIdPropietario(idPropietario);
    }
}

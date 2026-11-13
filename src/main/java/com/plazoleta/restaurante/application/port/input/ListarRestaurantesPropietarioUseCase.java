package com.plazoleta.restaurante.application.port.input;

import java.util.List;

import com.plazoleta.restaurante.domain.model.Restaurante;

public interface ListarRestaurantesPropietarioUseCase {

    List<Restaurante> listarRestaurantes(Long idPropietario);
}

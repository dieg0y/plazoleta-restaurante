package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.ListarRestaurantesUseCase;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.PaginaRestaurantes;
import lombok.RequiredArgsConstructor;

/**
 * CASO DE USO HU9: listar restaurantes para que el cliente
 * explore la plazoleta. Orden alfabetico, paginado y publico.
 */
@RequiredArgsConstructor
public class ListarRestaurantesService implements ListarRestaurantesUseCase {

    private final RestauranteRepositoryPort restauranteRepositoryPort;

    @Override
    public PaginaRestaurantes listarRestaurantes(int pagina, int tamano) {
        if (pagina < 0) {
            throw new BusinessException("El número de página no puede ser negativo");
        }
        if (tamano <= 0) {
            throw new BusinessException("El tamaño de página debe ser mayor a 0");
        }
        return restauranteRepositoryPort.listarOrdenadosPorNombre(pagina, tamano);
    }
}
package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.PaginaRestaurantes;

/**
 * PUERTO DE ENTRADA HU9: listar restaurantes para que el cliente
 * explore la plazoleta. Orden alfabetico, paginado y publico.
 */
public interface ListarRestaurantesUseCase {

    PaginaRestaurantes listarRestaurantes(int pagina, int tamano);
}
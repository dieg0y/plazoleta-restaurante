package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Restaurante;

/**
 * PUERTO DE ENTRADA HU9: listar restaurantes para que el cliente
 * explore la plazoleta. Orden alfabetico, paginado y publico.
 */
public interface ListarRestaurantesUseCase {

    Pagina<Restaurante> listarRestaurantes(int pagina, int tamano);
}
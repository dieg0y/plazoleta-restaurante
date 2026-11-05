package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Plato;

/**
 * PUERTO DE ENTRADA HU5: listar los platos activos de un
 * restaurante, con paginación y filtro opcional por categoría.
 */
public interface ListarPlatosUseCase {

    Pagina<Plato> listarPlatos(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano);

    Pagina<Plato> listarPlatos(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano,
            boolean incluirInactivos, Long idPropietario);
}
package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;

/**
 * PUERTO DE ENTRADA HU5: listar los platos activos de un
 * restaurante, con paginación y filtro opcional por categoría.
 */
public interface ListarPlatosUseCase {

    PaginaPlatos listarPlatos(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano);

    PaginaPlatos listarPlatos(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano,
            boolean incluirInactivos, Long idPropietario);
}
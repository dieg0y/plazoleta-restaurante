package com.plazoleta.restaurante.application.port.output;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;

public interface PlatoRepositoryPort {

    Plato guardar(Plato plato);

        // HU5: página de platos activos de un restaurante (categoría opcional)
    PaginaPlatos listarPorRestaurante(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano);
}
package com.plazoleta.restaurante.application.port.output;

import java.util.Optional;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;

public interface PlatoRepositoryPort {

    Plato guardar(Plato plato);

    Optional<Plato> obtenerPorId(Long id);

    PaginaPlatos listarPorRestaurante(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano);
}
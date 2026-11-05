package com.plazoleta.restaurante.application.port.output;

import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Restaurante;

import java.util.Optional;

public interface RestauranteRepositoryPort {

    Restaurante guardar(Restaurante restaurante);

    Optional<Restaurante> obtenerPorId(Long id);

    // HU9: orden alfabetico por nombre, resuelto por la base de datos
    Pagina<Restaurante> listarOrdenadosPorNombre(int pagina, int tamano);
}
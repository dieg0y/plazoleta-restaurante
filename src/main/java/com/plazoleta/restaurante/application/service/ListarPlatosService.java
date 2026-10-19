package com.plazoleta.restaurante.application.service;

import java.util.Objects;

import com.plazoleta.restaurante.application.port.input.ListarPlatosUseCase;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import lombok.RequiredArgsConstructor;

/**
 * CASO DE USO HU5: listar los platos activos de un restaurante.
 *
 * Reglas:
 *  1. Parámetros de paginación válidos (page >= 0, size > 0).
 *  2. El restaurante debe existir.
 *  3. El listado público muestra activos; el propietario puede incluir inactivos.
 *  4. Categoría opcional como filtro.
 */
@RequiredArgsConstructor
public class ListarPlatosService implements ListarPlatosUseCase {

    private final PlatoRepositoryPort platoRepositoryPort;
    private final RestauranteRepositoryPort restauranteRepositoryPort;

    @Override
    public PaginaPlatos listarPlatos(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano) {
        return listarPlatos(idRestaurante, categoria, pagina, tamano, false, null);
    }

    @Override
    public PaginaPlatos listarPlatos(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano,
            boolean incluirInactivos, Long idPropietario) {
        validarParametros(idRestaurante, pagina, tamano);

        var restaurante = restauranteRepositoryPort.obtenerPorId(idRestaurante)
                .orElseThrow(() -> new BusinessException(
                        "El restaurante con id " + idRestaurante + " no existe"));

        if (incluirInactivos) {
            if (idPropietario == null || !Objects.equals(restaurante.getIdPropietario(), idPropietario)) {
                throw new BusinessException(
                        "Solo el propietario del restaurante puede ver los platos deshabilitados");
            }
            return platoRepositoryPort.listarTodosPorRestaurante(idRestaurante, categoria, pagina, tamano);
        }

        return platoRepositoryPort.listarPorRestaurante(idRestaurante, categoria, pagina, tamano);
    }

    private void validarParametros(Long idRestaurante, int pagina, int tamano) {
        if (idRestaurante == null || idRestaurante <= 0) {
            throw new BusinessException("El id del restaurante es obligatorio");
        }
        if (pagina < 0) {
            throw new BusinessException("El número de página no puede ser negativo");
        }
        if (tamano <= 0) {
            throw new BusinessException("El tamaño de página debe ser mayor a 0");
        }
    }
}
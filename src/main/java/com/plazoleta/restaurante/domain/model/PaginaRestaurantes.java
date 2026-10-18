package com.plazoleta.restaurante.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HU9: página de resultados de restaurantes.
 * Wrapper del dominio para NO exponer tipos de Spring (Page)
 * en la capa de aplicación: la paginación "viaja" por los puertos
 * con un tipo propio del negocio.
 */
@Getter
@AllArgsConstructor
public class PaginaRestaurantes {

    private final List<Restaurante> contenido;
    private final int pagina;
    private final int tamano;
    private final long totalElementos;

    public int getTotalPaginas() {
        if (tamano <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElementos / tamano);
    }
}
package com.plazoleta.restaurante.infrastructure.in.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HU5: respuesta paginada de platos de un restaurante.
 * Reutiliza el PlatoResponse que ya tenés de HU3.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatoPaginaResponse {

    private List<PlatoResponse> contenido;
    private int pagina;
    private int tamano;
    private long totalElementos;
    private int totalPaginas;
}
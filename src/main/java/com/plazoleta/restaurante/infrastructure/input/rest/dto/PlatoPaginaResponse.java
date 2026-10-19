package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Pagina de platos de un restaurante")
public class PlatoPaginaResponse {

    @Schema(description = "Platos de la página")
    private List<PlatoResponse> contenido;
    @Schema(description = "Número de página base cero", example = "0")
    private int pagina;
    @Schema(description = "Tamaño de la página", example = "10")
    private int tamano;
    @Schema(description = "Cantidad total de platos", example = "18")
    private long totalElementos;
    @Schema(description = "Cantidad total de páginas", example = "2")
    private int totalPaginas;
}
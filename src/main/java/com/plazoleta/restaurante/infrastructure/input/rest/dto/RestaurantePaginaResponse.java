package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Página de restaurantes ordenados alfabéticamente")
public class RestaurantePaginaResponse {

    @Schema(description = "Restaurantes de la página; incluye el identificador técnico para navegación")
    private List<RestauranteResumenResponse> contenido;

    @Schema(description = "Número de página actual (base 0)", example = "0")
    private int pagina;

    @Schema(description = "Tamaño de la página", example = "10")
    private int tamano;

    @Schema(description = "Total de restaurantes", example = "25")
    private long totalElementos;

    @Schema(description = "Total de páginas", example = "3")
    private int totalPaginas;
}
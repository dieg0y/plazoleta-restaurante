package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.util.List;

import com.plazoleta.restaurante.domain.model.Pagina;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta genérica de una consulta paginada")
public record PageResponse<T>(
        @Schema(description = "Elementos de la página") List<T> contenido,
        @Schema(description = "Número de página base cero", example = "0") int pagina,
        @Schema(description = "Tamaño solicitado", example = "10") int tamano,
        @Schema(description = "Cantidad total de elementos", example = "28") long totalElementos,
        @Schema(description = "Cantidad total de páginas", example = "3") int totalPaginas) {

    public PageResponse {
        contenido = List.copyOf(contenido);
    }

    public static <T> PageResponse<T> from(Pagina<T> pagina) {
        return new PageResponse<>(
                pagina.contenido(),
                pagina.pagina(),
                pagina.tamano(),
                pagina.totalElementos(),
                pagina.totalPaginas());
    }
}

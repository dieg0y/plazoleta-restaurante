package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HU9: regla del enunciado — el listado publico muestra
 * El nombre y logo son los datos funcionales; el id permite navegar a la carta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen público de un restaurante para el listado")
public class RestauranteResumenResponse {

    @Schema(description = "Identificador técnico del restaurante", example = "1")
    private Long id;

    @Schema(description = "Nombre del restaurante", example = "Sabor Paisa")
    private String nombre;

    @Schema(description = "URL del logo del restaurante", example = "https://logo.com/fogon.png")
    private String urlLogo;
}
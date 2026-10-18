package com.plazoleta.restaurante.infrastructure.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HU9: regla del enunciado — el listado publico muestra
 * SOLO el nombre y la URL del logo del restaurante.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen público de un restaurante para el listado")
public class RestauranteResumenResponse {

    @Schema(description = "Nombre del restaurante", example = "Fogon de Pandi")
    private String nombre;

    @Schema(description = "URL del logo del restaurante", example = "https://logo.com/fogon.png")
    private String urlLogo;
}
package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Restaurante registrado")
public class RestauranteResponse {

    @Schema(description = "Identificador del restaurante")
    private Long id;
    @Schema(description = "Nombre")
    private String nombre;
    @Schema(description = "Direccion")
    private String direccion;
    @Schema(description = "Telefono")
    private String telefono;
    @Schema(description = "URL del logo")
    private String urlLogo;
    @Schema(description = "NIT")
    private String nit;
    @Schema(description = "Identificador del propietario")
    private Long idPropietario;
}
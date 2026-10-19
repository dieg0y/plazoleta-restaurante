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
@Schema(description = "Datos requeridos para registrar un restaurante (HU2)")
public class RestauranteRequest {

    @Schema(description = "Nombre no compuesto solo por numeros", example = "Sabor Paisa")
    private String nombre;
    @Schema(description = "Direccion del restaurante", example = "Calle 10 # 20-30")
    private String direccion;
    @Schema(description = "Telefono de hasta 13 caracteres", example = "+573000000002")
    private String telefono;
    @Schema(description = "URL del logo", example = "https://example.com/logo.png")
    private String urlLogo;
    @Schema(description = "NIT numerico", example = "900123456")
    private String nit;
    @Schema(description = "ID del usuario con rol PROPIETARIO", example = "2")
    private Long idPropietario;
}
package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear un plato (HU3)")
public class PlatoRequest {

    @Schema(description = "Nombre del plato", example = "Bandeja paisa")
    private String nombre;
    @Schema(description = "Categoria del plato", example = "PLATO_FUERTE")
    private CategoriaPlato categoria;
    @Schema(description = "Descripcion", example = "Plato tipico con carne, arroz y frijoles")
    private String descripcion;
    @Schema(description = "Precio entero positivo", example = "28000")
    private BigDecimal precio;
    @Schema(description = "URL de imagen", example = "https://example.com/bandeja.png")
    private String urlImagen;
    @Schema(description = "Restaurante al que pertenece", example = "1")
    private Long idRestaurante;
}
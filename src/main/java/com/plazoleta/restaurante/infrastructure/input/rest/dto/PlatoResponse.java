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
@Schema(description = "Plato registrado")
public class PlatoResponse {

    @Schema(description = "Identificador del plato")
    private Long id;
    @Schema(description = "Nombre")
    private String nombre;
    @Schema(description = "Categoria")
    private CategoriaPlato categoria;
    @Schema(description = "Descripcion")
    private String descripcion;
    @Schema(description = "Precio")
    private BigDecimal precio;
    @Schema(description = "URL de imagen")
    private String urlImagen;
    @Schema(description = "Identificador del restaurante")
    private Long idRestaurante;
    @Schema(description = "Indica si el plato esta activo")
    private Boolean activo;
}
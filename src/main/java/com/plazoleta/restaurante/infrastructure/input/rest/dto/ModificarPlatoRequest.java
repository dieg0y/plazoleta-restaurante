package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HU4: solo precio y descripción son modificables.
 * Que el DTO SOLO tenga estos dos campos es la garantía
 * arquitectónica de que nada más se puede cambiar.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Campos editables del plato (HU4)")
public class ModificarPlatoRequest {

    @Schema(description = "Nuevo precio entero positivo", example = "30000")
    private BigDecimal precio;

    @Schema(description = "Nueva descripcion", example = "Descripcion actualizada")
    private String descripcion;
}
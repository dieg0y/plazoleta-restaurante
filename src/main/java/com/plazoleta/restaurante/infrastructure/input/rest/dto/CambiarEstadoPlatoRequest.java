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
@Schema(description = "Estado solicitado para habilitar o deshabilitar un plato (HU7)")
public class CambiarEstadoPlatoRequest {

    @Schema(description = "Nuevo estado del plato", example = "false")
    private Boolean activo;
}
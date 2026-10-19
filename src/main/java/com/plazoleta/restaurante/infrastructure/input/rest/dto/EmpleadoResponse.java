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
@Schema(description = "Empleado asociado a un restaurante")
public class EmpleadoResponse {

    @Schema(description = "Identificador del empleado")
    private Long id;
    @Schema(description = "Identificador del usuario")
    private Long idUsuario;
    @Schema(description = "Identificador del restaurante")
    private Long idRestaurante;
}
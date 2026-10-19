package com.plazoleta.restaurante.infrastructure.output.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta del microservicio usuario: solo nos interesa el id creado. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta del microservicio usuarios")
public class EmpleadoRemotoResponse {

    @Schema(description = "ID del usuario creado")
    private Long id;
}
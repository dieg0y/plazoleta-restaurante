package com.plazoleta.restaurante.infrastructure.output.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos mínimos de usuario devueltos al microservicio restaurante")
public class UsuarioClienteResponse {

    @Schema(description = "Identificador del usuario")
    private Long id;
    @Schema(description = "Rol del usuario")
    private String rol;
}
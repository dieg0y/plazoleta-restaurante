package com.plazoleta.restaurante.infrastructure.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoResponse {

    private Long id;
    private Long idUsuario;
    private Long idRestaurante;
}
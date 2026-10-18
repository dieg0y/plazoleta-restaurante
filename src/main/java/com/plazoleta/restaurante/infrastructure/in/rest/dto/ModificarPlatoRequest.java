package com.plazoleta.restaurante.infrastructure.in.rest.dto;

import java.math.BigDecimal;

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
public class ModificarPlatoRequest {

    private BigDecimal precio;

    private String descripcion;
}
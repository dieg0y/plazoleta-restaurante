package com.plazoleta.restaurante.application.port.input;

import java.math.BigDecimal;

import com.plazoleta.restaurante.domain.model.Plato;

/**
 * PUERTO DE ENTRADA HU4: modificar precio y descripción de un plato.
 * Solo esos dos campos son modificables (regla del enunciado).
 */
public interface ModificarPlatoUseCase {

    Plato modificar(Long idPlato, BigDecimal precio, String descripcion, Long idPropietario);
}
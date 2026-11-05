package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EntregarPedidoRequest(
        @NotBlank @Pattern(regexp = "\\d{6}", message = "El PIN debe tener seis dígitos") String pin) {
}

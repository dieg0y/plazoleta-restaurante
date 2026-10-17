package com.plazoleta.restaurante.infrastructure.exception;

public record ErrorResponse(String mensaje, int status) {
}
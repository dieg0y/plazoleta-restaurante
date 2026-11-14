package com.plazoleta.restaurante.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    @Test
    void exponeElEstadoYMensajeControladosDelErrorSms() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.manejarSmsDeliveryException(
                new SmsDeliveryException(HttpStatus.SERVICE_UNAVAILABLE, "Twilio SMS no está configurado"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(503, response.getBody().status());
        assertEquals("Twilio SMS no está configurado", response.getBody().mensaje());
    }
}

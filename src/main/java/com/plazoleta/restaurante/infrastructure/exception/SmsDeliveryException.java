package com.plazoleta.restaurante.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class SmsDeliveryException extends RuntimeException {

    private final HttpStatus status;

    public SmsDeliveryException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

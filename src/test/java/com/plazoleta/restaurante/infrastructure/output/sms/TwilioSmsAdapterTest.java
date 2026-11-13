package com.plazoleta.restaurante.infrastructure.output.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.infrastructure.exception.SmsDeliveryException;

class TwilioSmsAdapterTest {

    @Test
    void fallaExplicitamenteSiTwilioNoEstaConfigurado() {
        TwilioSmsAdapter adapter = new TwilioSmsAdapter("", "", "");

        SmsDeliveryException exception = assertThrows(
                SmsDeliveryException.class,
                () -> adapter.enviar("+573001234567", "Pedido listo"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        assertTrue(exception.getMessage().contains("TWILIO_ACCOUNT_SID"));
    }

    @Test
    void rechazaDestinoSmsVacioAntesDeContactarTwilio() {
        TwilioSmsAdapter adapter = new TwilioSmsAdapter("AC123", "token", "+15005550006");

        assertThrows(BusinessException.class, () -> adapter.enviar("", "Pedido listo"));
    }

    @Test
    void incluyeCodigoYRecomendacionCuandoTwilioRechazaElRemitente() {
        HttpClientErrorException respuestaTwilio = new HttpClientErrorException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Unprocessable Entity",
                HttpHeaders.EMPTY,
                "{\"code\":21606,\"message\":\"mensaje privado del proveedor\"}"
                        .getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);

        SmsDeliveryException exception = TwilioSmsAdapter.mapearRechazoTwilio(respuestaTwilio);

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertTrue(exception.getMessage().contains("HTTP 422"));
        assertTrue(exception.getMessage().contains("21606"));
        assertTrue(exception.getMessage().contains("remitente"));
        assertFalse(exception.getMessage().contains("mensaje privado del proveedor"));
    }

    @Test
    void indicaCuandoTwilioRechazaLasCredenciales() {
        HttpClientErrorException respuestaTwilio = new HttpClientErrorException(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                HttpHeaders.EMPTY,
                "{\"code\":20003}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);

        SmsDeliveryException exception = TwilioSmsAdapter.mapearRechazoTwilio(respuestaTwilio);

        assertTrue(exception.getMessage().contains("20003"));
        assertTrue(exception.getMessage().contains("autenticación"));
    }
}

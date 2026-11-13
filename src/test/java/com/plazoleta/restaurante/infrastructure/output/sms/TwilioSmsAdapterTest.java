package com.plazoleta.restaurante.infrastructure.output.sms;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
}

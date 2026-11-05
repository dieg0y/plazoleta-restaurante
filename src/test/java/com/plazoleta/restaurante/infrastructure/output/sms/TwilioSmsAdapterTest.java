package com.plazoleta.restaurante.infrastructure.output.sms;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TwilioSmsAdapterTest {

    @Test
    void fallaExplicitamenteSiTwilioNoEstaConfigurado() {
        TwilioSmsAdapter adapter = new TwilioSmsAdapter("", "", "");

        assertThrows(IllegalStateException.class, () -> adapter.enviar("+573001234567", "Pedido listo"));
    }
}

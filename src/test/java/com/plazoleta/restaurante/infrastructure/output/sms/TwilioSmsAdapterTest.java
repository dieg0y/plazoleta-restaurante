package com.plazoleta.restaurante.infrastructure.output.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.plazoleta.restaurante.infrastructure.exception.SmsDeliveryException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

class TwilioSmsAdapterTest {

    @Test
    void fallaExplicitamenteSiTwilioSmsNoEstaConfigurado() {
        TwilioSmsAdapter adapter = new TwilioSmsAdapter(RestClient.builder(), "", "", "", "");

        SmsDeliveryException exception = assertThrows(
                SmsDeliveryException.class,
                () -> adapter.enviar("PIN 123456"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        assertTrue(exception.getMessage().contains("TWILIO_ACCOUNT_SID"));
        assertTrue(exception.getMessage().contains("TWILIO_PHONE_NUMBER"));
        assertTrue(exception.getMessage().contains("TWILIO_SMS_TO"));
    }

    @Test
    void enviaElSmsConLaApiDeMensajesDeTwilio() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        MultiValueMap<String, String> expectedForm = new LinkedMultiValueMap<>();
        expectedForm.add("To", "+573008172199");
        expectedForm.add("From", "+17372508034");
        expectedForm.add("Body", "Tu pedido 9 esta listo. PIN de seguridad: 123456");
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString("AC123:token".getBytes(StandardCharsets.UTF_8));

        server.expect(requestTo("https://api.twilio.com/2010-04-01/Accounts/AC123/Messages.json"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, basicAuth))
                .andExpect(content().formData(expectedForm))
                .andRespond(withSuccess());

        TwilioSmsAdapter adapter = new TwilioSmsAdapter(
                builder, "AC123", "token", "+17372508034", "+573008172199");
        adapter.enviar("Tu pedido 9 esta listo. PIN de seguridad: 123456");

        server.verify();
    }

    @Test
    void reintentaConLaPlantillaTrialCuandoTwilioRechazaElCuerpoPersonalizado() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        MultiValueMap<String, String> customForm = new LinkedMultiValueMap<>();
        customForm.add("To", "+573008172199");
        customForm.add("From", "+17372508034");
        customForm.add("Body", "Tu pedido 9 esta listo. PIN de seguridad: 123456");
        MultiValueMap<String, String> trialForm = new LinkedMultiValueMap<>();
        trialForm.add("To", "+573008172199");
        trialForm.add("From", "+17372508034");
        trialForm.add("Body", "sms_2fa");

        server.expect(requestTo("https://api.twilio.com/2010-04-01/Accounts/AC123/Messages.json"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formData(customForm))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"code\":572006,\"message\":\"Invalid template name\"}"));
        server.expect(requestTo("https://api.twilio.com/2010-04-01/Accounts/AC123/Messages.json"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formData(trialForm))
                .andRespond(withSuccess());

        TwilioSmsAdapter adapter = new TwilioSmsAdapter(
                builder, "AC123", "token", "+17372508034", "+573008172199");
        adapter.enviar("Tu pedido 9 esta listo. PIN de seguridad: 123456");

        server.verify();
    }

    @Test
    void incluyeCodigoYRecomendacionSinExponerLaRespuestaCrudaDelProveedor() {
        HttpClientErrorException respuestaTwilio = new HttpClientErrorException(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                HttpHeaders.EMPTY,
                "{\"code\":20003,\"message\":\"mensaje privado del proveedor\"}"
                        .getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);

        SmsDeliveryException exception = TwilioSmsAdapter.mapearRechazoTwilio(respuestaTwilio);

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertTrue(exception.getMessage().contains("HTTP 401"));
        assertTrue(exception.getMessage().contains("20003"));
        assertTrue(exception.getMessage().contains("autenticación"));
        assertFalse(exception.getMessage().contains("mensaje privado del proveedor"));
    }
}

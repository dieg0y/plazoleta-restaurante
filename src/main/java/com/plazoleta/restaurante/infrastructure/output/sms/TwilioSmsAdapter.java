package com.plazoleta.restaurante.infrastructure.output.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.restaurante.application.port.output.EnviarSmsPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.infrastructure.exception.SmsDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Equivale a {@code client.messages.create({ body, from, to })} de la API de
 * mensajes de Twilio: POST /2010-04-01/Accounts/{AccountSid}/Messages.json.
 */
@Component
public class TwilioSmsAdapter implements EnviarSmsPort {

    private static final ObjectMapper JSON_READER = new ObjectMapper();

    private final String accountSid;
    private final String authToken;
    private final String numeroOrigen;
    private final String numeroDestino;
    private final RestClient restClient;

    public TwilioSmsAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${app.sms.twilio.account-sid:}") String accountSid,
            @Value("${app.sms.twilio.auth-token:}") String authToken,
            @Value("${app.sms.twilio.from:}") String numeroOrigen,
            @Value("${app.sms.twilio.to:}") String numeroDestino) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.numeroOrigen = numeroOrigen;
        this.numeroDestino = numeroDestino;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void enviar(String mensaje) {
        if (accountSid.isBlank() || authToken.isBlank() || numeroOrigen.isBlank() || numeroDestino.isBlank()) {
            throw new SmsDeliveryException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Twilio SMS no está configurado. Define TWILIO_ACCOUNT_SID, "
                            + "TWILIO_AUTH_TOKEN, TWILIO_PHONE_NUMBER y TWILIO_SMS_TO, "
                            + "y reinicia Restaurante.");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new BusinessException("El mensaje SMS es obligatorio");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("To", numeroDestino);
        form.add("From", numeroOrigen);
        form.add("Body", mensaje);

        try {
            restClient.post()
                    .uri("https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json", accountSid)
                    .headers(headers -> headers.setBasicAuth(accountSid, authToken))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException excepcion) {
            throw mapearRechazoTwilio(excepcion);
        } catch (ResourceAccessException excepcion) {
            throw new SmsDeliveryException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con Twilio. Revisa la conexión e inténtalo de nuevo.");
        }
    }

    static SmsDeliveryException mapearRechazoTwilio(RestClientResponseException excepcion) {
        String codigo = extraerCodigoProveedor(excepcion.getResponseBodyAsString());
        String referencia = codigo.isBlank() ? "sin código Twilio" : "código Twilio " + codigo;
        String recomendacion = switch (codigo) {
            case "20003" -> "Twilio rechazó la autenticación; confirma el Account SID y el Auth Token vigentes.";
            case "21211", "21614" -> "Twilio no acepta el número destino; confirma el formato E.164.";
            case "21408" -> "Habilita en Twilio los permisos SMS para el país del destinatario.";
            case "21606" -> "El remitente no pertenece a esta cuenta o no puede enviar SMS.";
            case "21608" -> "La cuenta Trial solo entrega SMS a números verificados en Twilio.";
            case "21610" -> "El destinatario optó por no recibir mensajes de esta cuenta.";
            default -> "Revisa en Twilio Console la cuenta, el remitente y el destinatario del SMS.";
        };
        return new SmsDeliveryException(
                HttpStatus.BAD_GATEWAY,
                "Twilio rechazó el SMS (HTTP " + excepcion.getStatusCode().value()
                        + ", " + referencia + "). " + recomendacion);
    }

    private static String extraerCodigoProveedor(String responseBody) {
        try {
            JsonNode response = JSON_READER.readTree(responseBody);
            JsonNode code = response == null ? null : response.get("code");
            if (code != null && !code.isNull()) {
                String value = code.asText();
                if (value.matches("\\d{4,6}")) {
                    return value;
                }
            }
        } catch (JsonProcessingException excepcion) {
            return "";
        }
        return "";
    }
}

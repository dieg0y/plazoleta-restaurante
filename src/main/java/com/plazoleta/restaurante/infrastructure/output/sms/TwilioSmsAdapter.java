package com.plazoleta.restaurante.infrastructure.output.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.plazoleta.restaurante.application.port.output.EnviarSmsPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.infrastructure.exception.SmsDeliveryException;

@Component
public class TwilioSmsAdapter implements EnviarSmsPort {

    private static final ObjectMapper JSON_READER = new ObjectMapper();

    private final String accountSid;
    private final String authToken;
    private final String numeroOrigen;
    private final RestClient restClient = RestClient.create();

    public TwilioSmsAdapter(
            @Value("${app.sms.twilio.account-sid:}") String accountSid,
            @Value("${app.sms.twilio.auth-token:}") String authToken,
            @Value("${app.sms.twilio.from:}") String numeroOrigen) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.numeroOrigen = numeroOrigen;
    }

    @Override
    public void enviar(String celular, String mensaje) {
        if (accountSid.isBlank() || authToken.isBlank() || numeroOrigen.isBlank()) {
            throw new SmsDeliveryException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Twilio no está configurado. Define TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN y "
                            + "TWILIO_PHONE_NUMBER, y reinicia Restaurante.");
        }
        if (celular == null || celular.isBlank() || mensaje == null || mensaje.isBlank()) {
            throw new BusinessException("El destino y el mensaje SMS son obligatorios");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("To", celular);
        form.add("From", numeroOrigen);
        form.add("Body", mensaje);

        try {
            restClient.post()
                    .uri("https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json", accountSid)
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
            case "21211", "21614" -> "Twilio no acepta el número destino para SMS; confirma el número en formato E.164.";
            case "21408" -> "Habilita en Twilio los permisos SMS para el país del destinatario.";
            case "21606" -> "El remitente no pertenece a esta cuenta o no tiene capacidad para enviar SMS.";
            case "21608" -> "La cuenta Trial requiere que verifiques el número destinatario en Twilio.";
            case "21610" -> "El destinatario optó por no recibir mensajes de esta cuenta.";
            case "572002" -> "En una cuenta Trial, el remitente debe ser el número de Messaging asignado "
                    + "en Twilio Console a ese destinatario verificado.";
            default -> "Consulta este código en los registros de Twilio y revisa cuenta, remitente y destinatario.";
        };
        return new SmsDeliveryException(
                HttpStatus.BAD_GATEWAY,
                "Twilio rechazó la notificación (HTTP " + excepcion.getStatusCode().value()
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

package com.plazoleta.restaurante.infrastructure.output.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.plazoleta.restaurante.application.port.output.EnviarSmsPort;

@Component
public class TwilioSmsAdapter implements EnviarSmsPort {

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
            throw new IllegalStateException("Debe configurarse Twilio para enviar notificaciones SMS");
        }
        if (celular == null || celular.isBlank() || mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El destino y el mensaje SMS son obligatorios");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("To", celular);
        form.add("From", numeroOrigen);
        form.add("Body", mensaje);

        restClient.post()
                .uri("https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json", accountSid)
                .headers(headers -> headers.setBasicAuth(accountSid, authToken))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity();
    }
}

package com.plazoleta.restaurante.infrastructure.output.security;

import java.security.SecureRandom;
import java.util.Locale;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.application.port.output.CodigoSeguridadPedidoPort;

@Component
public class CodigoSeguridadPedidoAdapter implements CodigoSeguridadPedidoPort {

    private final SecureRandom secureRandom = new SecureRandom();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String generar() {
        return String.format(Locale.ROOT, "%06d", secureRandom.nextInt(1_000_000));
    }

    @Override
    public String codificar(String codigo) {
        return passwordEncoder.encode(codigo);
    }

    @Override
    public boolean coincide(String codigo, String codigoCodificado) {
        return codigo != null && codigoCodificado != null && passwordEncoder.matches(codigo, codigoCodificado);
    }
}

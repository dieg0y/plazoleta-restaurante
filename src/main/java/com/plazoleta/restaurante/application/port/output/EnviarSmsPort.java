package com.plazoleta.restaurante.application.port.output;

public interface EnviarSmsPort {

    void enviar(String celular, String mensaje);
}

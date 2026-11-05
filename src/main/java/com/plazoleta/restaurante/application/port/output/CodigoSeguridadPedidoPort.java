package com.plazoleta.restaurante.application.port.output;

public interface CodigoSeguridadPedidoPort {

    String generar();

    String codificar(String codigo);

    boolean coincide(String codigo, String codigoCodificado);
}

package com.plazoleta.restaurante.application.port.input;

import com.plazoleta.restaurante.domain.model.InformeEficienciaPedidos;

public interface ConsultarEficienciaPedidosUseCase {

    InformeEficienciaPedidos consultar(Long idRestaurante, Long idPropietario);
}

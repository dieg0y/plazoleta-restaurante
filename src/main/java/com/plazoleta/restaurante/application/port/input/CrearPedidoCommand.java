package com.plazoleta.restaurante.application.port.input;

import java.util.List;

public record CrearPedidoCommand(Long idRestaurante, List<PlatoSolicitado> platos) {

    public CrearPedidoCommand {
        platos = platos == null ? List.of() : List.copyOf(platos);
    }

    public record PlatoSolicitado(Long idPlato, int cantidad) {
    }
}

package com.plazoleta.restaurante.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Pedido {

    private final Long id;
    private final Long idCliente;
    private final Long idRestaurante;
    private final EstadoPedido estado;
    private final Long idEmpleadoAsignado;
    private final BigDecimal valorTotal;
    private final Instant fechaCreacion;
    private final Instant fechaFinalizacion;
    private final String codigoPinHash;
    private final List<PedidoPlato> platos;
}

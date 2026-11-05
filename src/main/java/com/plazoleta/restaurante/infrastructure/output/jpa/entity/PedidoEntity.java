package com.plazoleta.restaurante.infrastructure.output.jpa.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.plazoleta.restaurante.domain.model.EstadoPedido;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido", indexes = {
        @Index(name = "idx_pedido_cliente_estado", columnList = "id_cliente,estado"),
        @Index(name = "idx_pedido_restaurante_estado", columnList = "id_restaurante,estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "id_restaurante", nullable = false)
    private Long idRestaurante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(name = "id_empleado_asignado")
    private Long idEmpleadoAsignado;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_finalizacion")
    private Instant fechaFinalizacion;

    @Column(name = "codigo_pin_hash", length = 100)
    private String codigoPinHash;
}

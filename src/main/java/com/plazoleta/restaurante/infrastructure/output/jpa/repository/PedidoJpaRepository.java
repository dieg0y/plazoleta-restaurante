package com.plazoleta.restaurante.infrastructure.output.jpa.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PedidoEntity;

public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {

    boolean existsByIdClienteAndEstadoIn(Long idCliente, Collection<EstadoPedido> estados);
}

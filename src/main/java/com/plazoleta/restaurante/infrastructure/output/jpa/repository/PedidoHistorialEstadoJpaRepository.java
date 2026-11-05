package com.plazoleta.restaurante.infrastructure.output.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PedidoHistorialEstadoEntity;

public interface PedidoHistorialEstadoJpaRepository extends JpaRepository<PedidoHistorialEstadoEntity, Long> {

    List<PedidoHistorialEstadoEntity> findAllByIdPedidoOrderByFechaCambioAscIdAsc(Long idPedido);
}

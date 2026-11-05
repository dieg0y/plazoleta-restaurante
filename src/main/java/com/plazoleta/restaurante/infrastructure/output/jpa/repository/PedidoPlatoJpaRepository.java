package com.plazoleta.restaurante.infrastructure.output.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PedidoPlatoEntity;

public interface PedidoPlatoJpaRepository extends JpaRepository<PedidoPlatoEntity, Long> {

    List<PedidoPlatoEntity> findAllByIdPedidoOrderByIdAsc(Long idPedido);
}

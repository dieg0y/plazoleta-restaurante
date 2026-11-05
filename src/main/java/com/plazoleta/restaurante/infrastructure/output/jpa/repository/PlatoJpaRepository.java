package com.plazoleta.restaurante.infrastructure.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PlatoEntity;

public interface PlatoJpaRepository extends JpaRepository<PlatoEntity, Long> {
}
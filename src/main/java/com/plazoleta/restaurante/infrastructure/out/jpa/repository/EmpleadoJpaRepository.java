package com.plazoleta.restaurante.infrastructure.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plazoleta.restaurante.infrastructure.out.jpa.entity.EmpleadoEntity;

public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, Long> {
}
package com.plazoleta.restaurante.infrastructure.out.jpa.repository;

import com.plazoleta.restaurante.infrastructure.out.jpa.entity.PlatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatoJpaRepository extends JpaRepository<PlatoEntity, Long> {
}
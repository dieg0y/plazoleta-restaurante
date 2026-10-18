package com.plazoleta.restaurante.infrastructure.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.infrastructure.out.jpa.entity.PlatoEntity;

public interface PlatoJpaRepository extends JpaRepository<PlatoEntity, Long> {

    // HU5: platos ACTIVOS de un restaurante, paginados
    Page<PlatoEntity> findByIdRestauranteAndActivoTrue(Long idRestaurante, Pageable pageable);

    // HU5: ídem, filtrados por categoría
    Page<PlatoEntity> findByIdRestauranteAndCategoriaAndActivoTrue(Long idRestaurante,
                                                                   CategoriaPlato categoria,
                                                                   Pageable pageable);
}
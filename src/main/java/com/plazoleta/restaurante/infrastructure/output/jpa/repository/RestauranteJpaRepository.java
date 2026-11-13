package com.plazoleta.restaurante.infrastructure.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.plazoleta.restaurante.infrastructure.output.jpa.entity.RestauranteEntity;

@Repository
public interface RestauranteJpaRepository extends JpaRepository<RestauranteEntity, Long> {

    List<RestauranteEntity> findAllByIdPropietarioOrderByNombreAsc(Long idPropietario);
}
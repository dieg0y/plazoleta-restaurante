package com.plazoleta.restaurante.infrastructure.output.jpa.mapper;

import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PlatoEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoPersistenceMapper {

    PlatoEntity toEntity(Plato plato);

    Plato toDomain(PlatoEntity entity);
}
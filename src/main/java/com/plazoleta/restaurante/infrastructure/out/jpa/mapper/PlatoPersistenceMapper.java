package com.plazoleta.restaurante.infrastructure.out.jpa.mapper;

import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.out.jpa.entity.PlatoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoPersistenceMapper {

    PlatoEntity toEntity(Plato plato);

    Plato toDomain(PlatoEntity entity);
}
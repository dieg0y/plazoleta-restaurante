package com.plazoleta.restaurante.infrastructure.output.jpa.mapper;

import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.RestauranteEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestaurantePersistenceMapper {

    RestauranteEntity toEntity(Restaurante restaurante);

    Restaurante toDomain(RestauranteEntity entity);
}
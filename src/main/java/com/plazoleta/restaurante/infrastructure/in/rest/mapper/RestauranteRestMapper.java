package com.plazoleta.restaurante.infrastructure.in.rest.mapper;

import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestauranteRestMapper {

    @Mapping(target = "id", ignore = true)
    Restaurante toDomain(RestauranteRequest request);

    RestauranteResponse toResponse(Restaurante restaurante);
}
package com.plazoleta.restaurante.infrastructure.in.rest.mapper;

import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatoRestMapper {

    @Mapping(target = "id", ignore = true)
    Plato toDomain(PlatoRequest request);

    PlatoResponse toResponse(Plato plato);
}
package com.plazoleta.restaurante.infrastructure.input.rest.mapper;

import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PageResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PlatoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PlatoResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlatoRestMapper {

    @Mapping(target = "id", ignore = true)
    Plato toDomain(PlatoRequest request);

    PlatoResponse toResponse(Plato plato);

    // ===== HU5: listar platos =====

    List<PlatoResponse> toResponseList(List<Plato> platos);

    default PageResponse<PlatoResponse> toPageResponse(Pagina<Plato> pagina) {
        return PageResponse.from(pagina.map(this::toResponse));
    }
}
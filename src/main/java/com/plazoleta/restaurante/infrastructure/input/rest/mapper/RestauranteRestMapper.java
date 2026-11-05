package com.plazoleta.restaurante.infrastructure.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PageResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteResumenResponse;

@Mapper(componentModel = "spring")
public interface RestauranteRestMapper {

    @Mapping(target = "id", ignore = true)
    Restaurante toDomain(RestauranteRequest request);

    RestauranteResponse toResponse(Restaurante restaurante);

    // ===== HU9: listado público =====

    // Regla del enunciado: el listado devuelve SOLO nombre y URL del logo.
    // MapStruct mapea esos dos campos y descarta el resto (nit, direccion,
    // telefono, idPropietario) porque el target solo los tiene a esos dos.
    RestauranteResumenResponse toResumenResponse(Restaurante restaurante);

    List<RestauranteResumenResponse> toResumenResponseList(List<Restaurante> restaurantes);

    default PageResponse<RestauranteResumenResponse> toPageResponse(Pagina<Restaurante> pagina) {
        return PageResponse.from(pagina.map(this::toResumenResponse));
    }
}
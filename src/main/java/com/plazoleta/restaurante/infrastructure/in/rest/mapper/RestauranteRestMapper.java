package com.plazoleta.restaurante.infrastructure.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.plazoleta.restaurante.domain.model.PaginaRestaurantes;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestaurantePaginaResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteResumenResponse;

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

    // MapStruct usa toResumenResponse automáticamente para cada elemento de la lista
    RestaurantePaginaResponse toPaginaResponse(PaginaRestaurantes pagina);
}
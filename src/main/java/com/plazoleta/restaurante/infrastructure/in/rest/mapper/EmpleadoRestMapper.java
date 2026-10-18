package com.plazoleta.restaurante.infrastructure.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.CrearEmpleadoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.EmpleadoResponse;

@Mapper(componentModel = "spring")
public interface EmpleadoRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    Empleado toDomain(CrearEmpleadoRequest request);

    EmpleadoResponse toResponse(Empleado empleado);
}
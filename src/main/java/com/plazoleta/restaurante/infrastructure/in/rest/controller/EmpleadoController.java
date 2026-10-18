package com.plazoleta.restaurante.infrastructure.in.rest.controller;

import com.plazoleta.restaurante.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.CrearEmpleadoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.EmpleadoResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.mapper.EmpleadoRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoRestMapper empleadoRestMapper;
    private final CrearEmpleadoUseCase crearEmpleadoUseCase;

    // TODO HU5: idPropietario saldra del token JWT, no del query param
    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(
            @RequestParam Long idPropietario,
            @RequestBody CrearEmpleadoRequest request) {
        Empleado empleado = empleadoRestMapper.toDomain(request);
        Empleado registrado = crearEmpleadoUseCase.crearEmpleado(empleado, idPropietario);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoRestMapper.toResponse(registrado));
    }
}
package com.plazoleta.restaurante.infrastructure.input.rest;

import com.plazoleta.restaurante.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.infrastructure.configuration.UsuarioAutenticado;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.CrearEmpleadoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.EmpleadoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.mapper.EmpleadoRestMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Empleados", description = "Cuentas de empleados del restaurante")
@RestController
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoRestMapper empleadoRestMapper;
    private final CrearEmpleadoUseCase crearEmpleadoUseCase;

    @Operation(summary = "Crear una cuenta de empleado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empleado creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Se requiere propietario del restaurante")
    })
    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestBody CrearEmpleadoRequest request) {
        Empleado empleado = empleadoRestMapper.toDomain(request);
        Empleado registrado = crearEmpleadoUseCase.crearEmpleado(empleado, usuario.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoRestMapper.toResponse(registrado));
    }
}
package com.plazoleta.restaurante.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.port.input.CambiarEstadoPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPlatosUseCase;
import com.plazoleta.restaurante.application.port.input.ModificarPlatoUseCase;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.configuration.UsuarioAutenticado;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.CambiarEstadoPlatoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.ModificarPlatoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PlatoPaginaResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PlatoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PlatoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.mapper.PlatoRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Platos", description = "Gestión y consulta de platos por restaurante")
@RestController
@RequestMapping("/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoRestMapper platoRestMapper;
    private final CrearPlatoUseCase crearPlatoUseCase;
    private final ListarPlatosUseCase listarPlatosUseCase;
    private final ModificarPlatoUseCase modificarPlatoUseCase;
    private final CambiarEstadoPlatoUseCase cambiarEstadoPlatoUseCase;

    // HU5: el idPropietario sale del token JWT, ya no del query param
    @Operation(summary = "Crear un plato para el restaurante del propietario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plato creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Solo el propietario del restaurante")
    })
    @PostMapping
    public ResponseEntity<PlatoResponse> crearPlato(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestBody PlatoRequest request) {
        Plato plato = platoRestMapper.toDomain(request);
        Plato guardado = crearPlatoUseCase.crear(plato, usuario.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(platoRestMapper.toResponse(guardado));
    }

    // El listado publico muestra platos activos; el propietario puede incluir inactivos.
    @Operation(summary = "Listar platos activos de un restaurante")
    @GetMapping
    public ResponseEntity<PlatoPaginaResponse> listarPlatos(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam Long idRestaurante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CategoriaPlato categoria,
            @RequestParam(defaultValue = "false") boolean todos) {
        if (todos && usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }
        if (todos && !"PROPIETARIO".equals(usuario.rol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el propietario puede ver los platos deshabilitados");
        }
        PaginaPlatos pagina = listarPlatosUseCase.listarPlatos(
                idRestaurante, categoria, page, size, todos, todos ? usuario.id() : null);
        return ResponseEntity.ok(platoRestMapper.toPaginaResponse(pagina));
    }

    // HU4: modificar SOLO precio y descripcion - HU5: el dueno sale del token
    @Operation(summary = "Modificar precio y descripción de un plato")
    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponse> modificarPlato(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id,
            @RequestBody ModificarPlatoRequest request) {
        Plato actualizado = modificarPlatoUseCase.modificar(
                id, request.getPrecio(), request.getDescripcion(), usuario.id());
        return ResponseEntity.ok(platoRestMapper.toResponse(actualizado));
    }

    // HU7: habilitar/deshabilitar plato - el dueno sale del token
    @Operation(summary = "Habilitar o deshabilitar un plato")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PlatoResponse> cambiarEstadoPlato(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id,
            @RequestBody CambiarEstadoPlatoRequest request) {
        Plato actualizado = cambiarEstadoPlatoUseCase.cambiarEstado(
                id, request.getActivo(), usuario.id());
        return ResponseEntity.ok(platoRestMapper.toResponse(actualizado));
    }
}
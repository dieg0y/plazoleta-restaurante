package com.plazoleta.restaurante.infrastructure.in.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPlatosUseCase;
import com.plazoleta.restaurante.application.port.input.ModificarPlatoUseCase;
import com.plazoleta.restaurante.configuration.UsuarioAutenticado;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.ModificarPlatoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoPaginaResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.mapper.PlatoRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoRestMapper platoRestMapper;
    private final CrearPlatoUseCase crearPlatoUseCase;
    private final ListarPlatosUseCase listarPlatosUseCase;
    private final ModificarPlatoUseCase modificarPlatoUseCase;

    // HU5: el idPropietario sale del token JWT, ya no del query param
    @PostMapping
    public ResponseEntity<PlatoResponse> crearPlato(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestBody PlatoRequest request) {
        Plato plato = platoRestMapper.toDomain(request);
        Plato guardado = crearPlatoUseCase.crear(plato, usuario.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(platoRestMapper.toResponse(guardado));
    }

    // Listado paginado de platos activos (categoría opcional) — público
    @GetMapping
    public ResponseEntity<PlatoPaginaResponse> listarPlatos(
            @RequestParam Long idRestaurante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CategoriaPlato categoria) {
        PaginaPlatos pagina = listarPlatosUseCase.listarPlatos(idRestaurante, categoria, page, size);
        return ResponseEntity.ok(platoRestMapper.toPaginaResponse(pagina));
    }

    // HU4: modificar SOLO precio y descripción — HU5: el dueño sale del token
    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponse> modificarPlato(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id,
            @RequestBody ModificarPlatoRequest request) {
        Plato actualizado = modificarPlatoUseCase.modificar(
                id, request.getPrecio(), request.getDescripcion(), usuario.id());
        return ResponseEntity.ok(platoRestMapper.toResponse(actualizado));
    }
}
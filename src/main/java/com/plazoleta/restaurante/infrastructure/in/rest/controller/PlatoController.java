package com.plazoleta.restaurante.infrastructure.in.rest.controller;

import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPlatosUseCase;
import com.plazoleta.restaurante.application.port.input.ModificarPlatoUseCase;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.ModificarPlatoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoPaginaResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.mapper.PlatoRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoRestMapper platoRestMapper;
    private final CrearPlatoUseCase crearPlatoUseCase;
    private final ListarPlatosUseCase listarPlatosUseCase;
    private final ModificarPlatoUseCase modificarPlatoUseCase;

    // TODO HU5: idPropietario saldra del token JWT, no del query param
    @PostMapping
    public ResponseEntity<PlatoResponse> crearPlato(
            @RequestParam Long idPropietario,
            @RequestBody PlatoRequest request) {
        Plato plato = platoRestMapper.toDomain(request);
        Plato guardado = crearPlatoUseCase.crear(plato, idPropietario);
        return ResponseEntity.status(HttpStatus.CREATED).body(platoRestMapper.toResponse(guardado));
    }

    // Listado paginado de platos activos (categoría opcional)
    @GetMapping
    public ResponseEntity<PlatoPaginaResponse> listarPlatos(
            @RequestParam Long idRestaurante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CategoriaPlato categoria) {
        PaginaPlatos pagina = listarPlatosUseCase.listarPlatos(idRestaurante, categoria, page, size);
        return ResponseEntity.ok(platoRestMapper.toPaginaResponse(pagina));
    }

    // HU4: modificar SOLO precio y descripción
    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponse> modificarPlato(
            @PathVariable Long id,
            @RequestParam Long idPropietario,
            @RequestBody ModificarPlatoRequest request) {
        Plato actualizado = modificarPlatoUseCase.modificar(
                id, request.getPrecio(), request.getDescripcion(), idPropietario);
        return ResponseEntity.ok(platoRestMapper.toResponse(actualizado));
    }
}
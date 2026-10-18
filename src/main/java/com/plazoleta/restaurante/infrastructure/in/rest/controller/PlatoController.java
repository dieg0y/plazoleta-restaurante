package com.plazoleta.restaurante.infrastructure.in.rest.controller;

import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPlatosUseCase;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoPaginaResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.mapper.PlatoRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    // TODO HU6: idPropietario saldra del token JWT, no del query param
    @PostMapping
    public ResponseEntity<PlatoResponse> crearPlato(
            @RequestParam Long idPropietario,
            @RequestBody PlatoRequest request) {
        Plato plato = platoRestMapper.toDomain(request);
        Plato guardado = crearPlatoUseCase.crear(plato, idPropietario);
        return ResponseEntity.status(HttpStatus.CREATED).body(platoRestMapper.toResponse(guardado));
    }

    // HU5: lista paginada de platos ACTIVOS de un restaurante (categoría opcional)
    @GetMapping
    public ResponseEntity<PlatoPaginaResponse> listarPlatos(
            @RequestParam Long idRestaurante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CategoriaPlato categoria) {
        PaginaPlatos pagina = listarPlatosUseCase.listarPlatos(idRestaurante, categoria, page, size);
        return ResponseEntity.ok(platoRestMapper.toPaginaResponse(pagina));
    }
}
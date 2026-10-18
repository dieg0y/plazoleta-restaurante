package com.plazoleta.restaurante.infrastructure.in.rest.controller;

import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.PlatoResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.mapper.PlatoRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // TODO HU5: idPropietario saldra del token JWT, no del query param
    @PostMapping
    public ResponseEntity<PlatoResponse> crearPlato(
            @RequestParam Long idPropietario,
            @RequestBody PlatoRequest request) {
        Plato plato = platoRestMapper.toDomain(request);
        Plato guardado = crearPlatoUseCase.crear(plato, idPropietario);
        return ResponseEntity.status(HttpStatus.CREATED).body(platoRestMapper.toResponse(guardado));
    }
}
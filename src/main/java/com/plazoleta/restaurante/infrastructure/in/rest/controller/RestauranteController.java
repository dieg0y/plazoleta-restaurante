package com.plazoleta.restaurante.infrastructure.in.rest.controller;

import com.plazoleta.restaurante.application.port.input.CrearRestauranteUseCase;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.mapper.RestauranteRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteRestMapper restauranteRestMapper;
    private final CrearRestauranteUseCase crearRestauranteUseCase;

    @PostMapping
    public ResponseEntity<RestauranteResponse> crearRestaurante(@RequestBody RestauranteRequest request) {
        Restaurante restaurante = restauranteRestMapper.toDomain(request);
        Restaurante guardado = crearRestauranteUseCase.crear(restaurante);
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteRestMapper.toResponse(guardado));
    }
}
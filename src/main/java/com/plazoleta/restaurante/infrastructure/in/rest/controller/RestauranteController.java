package com.plazoleta.restaurante.infrastructure.in.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.port.input.CrearRestauranteUseCase;
import com.plazoleta.restaurante.application.port.input.ListarRestaurantesUseCase;
import com.plazoleta.restaurante.domain.model.PaginaRestaurantes;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestaurantePaginaResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteRequest;
import com.plazoleta.restaurante.infrastructure.in.rest.dto.RestauranteResponse;
import com.plazoleta.restaurante.infrastructure.in.rest.mapper.RestauranteRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteRestMapper restauranteRestMapper;
    private final CrearRestauranteUseCase crearRestauranteUseCase;
    private final ListarRestaurantesUseCase listarRestaurantesUseCase;

    @PostMapping
    public ResponseEntity<RestauranteResponse> crearRestaurante(@RequestBody RestauranteRequest request) {
        Restaurante restaurante = restauranteRestMapper.toDomain(request);
        Restaurante guardado = crearRestauranteUseCase.crear(restaurante);
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteRestMapper.toResponse(guardado));
    }

    // HU9: listado público, alfabético y paginado (solo nombre y logo)
    @GetMapping
    public ResponseEntity<RestaurantePaginaResponse> listarRestaurantes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginaRestaurantes pagina = listarRestaurantesUseCase.listarRestaurantes(page, size);
        return ResponseEntity.ok(restauranteRestMapper.toPaginaResponse(pagina));
    }
}
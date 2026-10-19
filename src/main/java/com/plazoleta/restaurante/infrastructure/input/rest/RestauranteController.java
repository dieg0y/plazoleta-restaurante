package com.plazoleta.restaurante.infrastructure.input.rest;

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
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestaurantePaginaResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.mapper.RestauranteRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Restaurantes", description = "Creación y consulta paginada de restaurantes")
@RestController
@RequestMapping("/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteRestMapper restauranteRestMapper;
    private final CrearRestauranteUseCase crearRestauranteUseCase;
    private final ListarRestaurantesUseCase listarRestaurantesUseCase;

    @Operation(summary = "Crear un restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o propietario no válido"),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR")
    })
    @PostMapping
    public ResponseEntity<RestauranteResponse> crearRestaurante(@RequestBody RestauranteRequest request) {
        Restaurante restaurante = restauranteRestMapper.toDomain(request);
        Restaurante guardado = crearRestauranteUseCase.crear(restaurante);
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteRestMapper.toResponse(guardado));
    }

    // HU9: listado público, alfabético y paginado (solo nombre y logo)
    @Operation(summary = "Listar restaurantes en orden alfabético")
    @GetMapping
    public ResponseEntity<RestaurantePaginaResponse> listarRestaurantes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginaRestaurantes pagina = listarRestaurantesUseCase.listarRestaurantes(page, size);
        return ResponseEntity.ok(restauranteRestMapper.toPaginaResponse(pagina));
    }
}
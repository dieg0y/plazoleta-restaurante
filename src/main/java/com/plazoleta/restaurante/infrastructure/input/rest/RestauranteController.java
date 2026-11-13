package com.plazoleta.restaurante.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.port.input.CrearRestauranteUseCase;
import com.plazoleta.restaurante.application.port.input.ListarRestaurantesUseCase;
import com.plazoleta.restaurante.application.port.input.ListarRestaurantesPropietarioUseCase;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.configuration.UsuarioAutenticado;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PageResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.RestauranteResumenResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.mapper.RestauranteRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Tag(name = "Restaurantes", description = "Creación y consulta paginada de restaurantes")
@RestController
@RequestMapping("/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteRestMapper restauranteRestMapper;
    private final CrearRestauranteUseCase crearRestauranteUseCase;
    private final ListarRestaurantesUseCase listarRestaurantesUseCase;
    private final ListarRestaurantesPropietarioUseCase listarRestaurantesPropietarioUseCase;

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
    public ResponseEntity<PageResponse<RestauranteResumenResponse>> listarRestaurantes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pagina<Restaurante> pagina = listarRestaurantesUseCase.listarRestaurantes(page, size);
        return ResponseEntity.ok(restauranteRestMapper.toPageResponse(pagina));
    }

    @Operation(summary = "Listar restaurantes del propietario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurantes del propietario"),
            @ApiResponse(responseCode = "401", description = "Se requiere autenticación"),
            @ApiResponse(responseCode = "403", description = "Se requiere rol PROPIETARIO")
    })
    @GetMapping("/mio")
    public ResponseEntity<List<RestauranteResponse>> listarMisRestaurantes(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        List<RestauranteResponse> restaurantes = listarRestaurantesPropietarioUseCase
                .listarRestaurantes(usuario.id())
                .stream()
                .map(restauranteRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(restaurantes);
    }
}
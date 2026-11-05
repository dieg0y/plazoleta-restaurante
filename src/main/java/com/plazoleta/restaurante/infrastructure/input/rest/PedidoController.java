package com.plazoleta.restaurante.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.restaurante.application.port.input.CrearPedidoCommand;
import com.plazoleta.restaurante.application.port.input.CrearPedidoUseCase;
import com.plazoleta.restaurante.infrastructure.configuration.UsuarioAutenticado;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.CrearPedidoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PedidoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.mapper.PedidoRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Pedidos", description = "Creación y seguimiento de pedidos")
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final PedidoRestMapper pedidoRestMapper;

    @Operation(summary = "Crear un pedido para un solo restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado en estado PENDIENTE"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pedido activo existente"),
            @ApiResponse(responseCode = "403", description = "Se requiere rol CLIENTE")
    })
    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @Valid @RequestBody CrearPedidoRequest request) {
        CrearPedidoCommand command = new CrearPedidoCommand(
                request.idRestaurante(),
                request.platos().stream()
                        .map(plato -> new CrearPedidoCommand.PlatoSolicitado(plato.idPlato(), plato.cantidad()))
                        .toList());
        PedidoResponse response = pedidoRestMapper.toResponse(
                crearPedidoUseCase.crear(usuario.id(), command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package com.plazoleta.restaurante.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.plazoleta.restaurante.application.port.input.CrearPedidoCommand;
import com.plazoleta.restaurante.application.port.input.CrearPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.AsignarPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPedidosUseCase;
import com.plazoleta.restaurante.application.port.input.MarcarPedidoListoUseCase;
import com.plazoleta.restaurante.infrastructure.configuration.UsuarioAutenticado;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.CrearPedidoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PageResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PedidoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.mapper.PedidoRestMapper;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;
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
    private final AsignarPedidoUseCase asignarPedidoUseCase;
    private final MarcarPedidoListoUseCase marcarPedidoListoUseCase;
    private final ListarPedidosUseCase listarPedidosUseCase;
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

    @Operation(summary = "Listar pedidos del restaurante del empleado por estado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos del restaurante en la página solicitada"),
            @ApiResponse(responseCode = "400", description = "Estado o paginación inválidos"),
            @ApiResponse(responseCode = "403", description = "Se requiere rol EMPLEADO")
    })
    @GetMapping
    public ResponseEntity<PageResponse<PedidoResponse>> listarPedidos(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam EstadoPedido estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pagina<Pedido> pagina = listarPedidosUseCase.listar(usuario.id(), estado, page, size);
        return ResponseEntity.ok(PageResponse.from(pagina.map(pedidoRestMapper::toResponse)));
    }

    @Operation(summary = "Asignarse a un pedido pendiente e iniciar su preparación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido asignado y en preparación"),
            @ApiResponse(responseCode = "400", description = "El pedido no está disponible para asignación"),
            @ApiResponse(responseCode = "403", description = "Se requiere un empleado del restaurante")
    })
    @PatchMapping("/{id}/asignar")
    public ResponseEntity<PedidoResponse> asignarPedido(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id) {
        Pedido pedido = asignarPedidoUseCase.asignar(id, usuario.id());
        return ResponseEntity.ok(pedidoRestMapper.toResponse(pedido));
    }

    @Operation(summary = "Notificar al cliente cuando su pedido esté listo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido listo y PIN enviado por SMS"),
            @ApiResponse(responseCode = "400", description = "El pedido no está en preparación"),
            @ApiResponse(responseCode = "403", description = "Se requiere el empleado asignado")
    })
    @PatchMapping("/{id}/listo")
    public ResponseEntity<PedidoResponse> marcarPedidoListo(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id) {
        Pedido pedido = marcarPedidoListoUseCase.marcarListo(id, usuario.id());
        return ResponseEntity.ok(pedidoRestMapper.toResponse(pedido));
    }
}

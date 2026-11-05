package com.plazoleta.restaurante.infrastructure.input.rest;

import java.util.List;

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
import com.plazoleta.restaurante.application.port.input.CancelarPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.ConsultarEficienciaPedidosUseCase;
import com.plazoleta.restaurante.application.port.input.ConsultarTrazabilidadPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.EntregarPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPedidosUseCase;
import com.plazoleta.restaurante.application.port.input.MarcarPedidoListoUseCase;
import com.plazoleta.restaurante.infrastructure.configuration.UsuarioAutenticado;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.CrearPedidoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.EntregarPedidoRequest;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.DuracionPedidoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.EficienciaPedidosResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.HistorialEstadoPedidoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PageResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PedidoResponse;
import com.plazoleta.restaurante.infrastructure.input.rest.dto.PromedioTiempoEmpleadoResponse;
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
    private final EntregarPedidoUseCase entregarPedidoUseCase;
    private final CancelarPedidoUseCase cancelarPedidoUseCase;
    private final ConsultarTrazabilidadPedidoUseCase consultarTrazabilidadPedidoUseCase;
    private final ConsultarEficienciaPedidosUseCase consultarEficienciaPedidosUseCase;
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

    @Operation(summary = "Validar el PIN y marcar un pedido listo como entregado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido entregado"),
            @ApiResponse(responseCode = "400", description = "Pedido no listo o PIN incorrecto"),
            @ApiResponse(responseCode = "403", description = "Se requiere un empleado del restaurante")
    })
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<PedidoResponse> entregarPedido(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id,
            @Valid @RequestBody EntregarPedidoRequest request) {
        Pedido pedido = entregarPedidoUseCase.entregar(id, usuario.id(), request.pin());
        return ResponseEntity.ok(pedidoRestMapper.toResponse(pedido));
    }

    @Operation(summary = "Cancelar un pedido pendiente del cliente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
            @ApiResponse(responseCode = "400", description = "Pedido no pendiente o no pertenece al cliente"),
            @ApiResponse(responseCode = "403", description = "Se requiere rol CLIENTE")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelarPedido(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id) {
        Pedido pedido = cancelarPedidoUseCase.cancelar(id, usuario.id());
        return ResponseEntity.ok(pedidoRestMapper.toResponse(pedido));
    }

    @Operation(summary = "Consultar los cambios de estado de un pedido propio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial de estados del pedido"),
            @ApiResponse(responseCode = "400", description = "El pedido no existe o no pertenece al cliente"),
            @ApiResponse(responseCode = "403", description = "Se requiere rol CLIENTE")
    })
    @GetMapping("/{id}/trazabilidad")
    public ResponseEntity<List<HistorialEstadoPedidoResponse>> consultarTrazabilidad(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id) {
        List<HistorialEstadoPedidoResponse> historial = consultarTrazabilidadPedidoUseCase
                .consultar(id, usuario.id())
                .stream()
                .map(estado -> new HistorialEstadoPedidoResponse(
                        estado.id(), estado.estado(), estado.fechaCambio()))
                .toList();
        return ResponseEntity.ok(historial);
    }

    @Operation(summary = "Consultar tiempos de pedidos y ranking promedio de empleados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tiempos de pedidos entregados del restaurante"),
            @ApiResponse(responseCode = "400", description = "Restaurante inválido o fuera de la propiedad"),
            @ApiResponse(responseCode = "403", description = "Se requiere rol PROPIETARIO")
    })
    @GetMapping("/eficiencia")
    public ResponseEntity<EficienciaPedidosResponse> consultarEficiencia(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam Long idRestaurante) {
        var informe = consultarEficienciaPedidosUseCase.consultar(idRestaurante, usuario.id());
        EficienciaPedidosResponse response = new EficienciaPedidosResponse(
                informe.pedidos().stream()
                        .map(pedido -> new DuracionPedidoResponse(
                                pedido.idPedido(), pedido.idEmpleado(), pedido.duracionSegundos()))
                        .toList(),
                informe.ranking().stream()
                        .map(empleado -> new PromedioTiempoEmpleadoResponse(
                                empleado.idEmpleado(),
                                empleado.pedidosEntregados(),
                                empleado.promedioDuracionSegundos(),
                                empleado.posicion()))
                        .toList());
        return ResponseEntity.ok(response);
    }
}

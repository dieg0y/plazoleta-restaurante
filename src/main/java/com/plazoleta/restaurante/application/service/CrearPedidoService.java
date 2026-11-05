package com.plazoleta.restaurante.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.plazoleta.restaurante.application.port.input.CrearPedidoCommand;
import com.plazoleta.restaurante.application.port.input.CrearPedidoUseCase;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;
import com.plazoleta.restaurante.domain.model.PedidoPlato;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class CrearPedidoService implements CrearPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final PlatoRepositoryPort platoRepositoryPort;
    private final RestauranteRepositoryPort restauranteRepositoryPort;

    @Override
    @Transactional
    public Pedido crear(Long idCliente, CrearPedidoCommand command) {
        if (idCliente == null || idCliente <= 0) {
            throw new BusinessException("El cliente autenticado no es válido");
        }
        if (command == null || command.idRestaurante() == null || command.idRestaurante() <= 0) {
            throw new BusinessException("El restaurante es obligatorio");
        }
        if (command.platos().isEmpty()) {
            throw new BusinessException("El pedido debe incluir al menos un plato");
        }
        if (pedidoRepositoryPort.existePedidoActivoPorCliente(idCliente)) {
            throw new BusinessException("El cliente ya tiene un pedido en proceso");
        }

        restauranteRepositoryPort.obtenerPorId(command.idRestaurante())
                .orElseThrow(() -> new BusinessException(
                        "El restaurante con id " + command.idRestaurante() + " no existe"));

        Set<Long> idsPlatos = new HashSet<>();
        List<PedidoPlato> platos = command.platos().stream()
                .map(solicitud -> {
                    if (solicitud.idPlato() == null || solicitud.idPlato() <= 0 || solicitud.cantidad() <= 0) {
                        throw new BusinessException("Cada plato debe tener un id y una cantidad mayores a 0");
                    }
                    if (!idsPlatos.add(solicitud.idPlato())) {
                        throw new BusinessException("No se permiten platos duplicados en el pedido");
                    }
                    var plato = platoRepositoryPort.obtenerPorId(solicitud.idPlato())
                            .orElseThrow(() -> new BusinessException(
                                    "El plato con id " + solicitud.idPlato() + " no existe"));
                    if (!command.idRestaurante().equals(plato.getIdRestaurante())) {
                        throw new BusinessException("Todos los platos deben pertenecer al mismo restaurante");
                    }
                    if (!Boolean.TRUE.equals(plato.getActivo())) {
                        throw new BusinessException("El plato " + plato.getNombre() + " no está disponible");
                    }
                    return new PedidoPlato(
                            plato.getId(), plato.getNombre(), plato.getPrecio(), solicitud.cantidad());
                })
                .toList();

        BigDecimal valorTotal = platos.stream()
                .map(plato -> plato.precioUnitario().multiply(BigDecimal.valueOf(plato.cantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = Pedido.builder()
                .idCliente(idCliente)
                .idRestaurante(command.idRestaurante())
                .estado(EstadoPedido.PENDIENTE)
                .valorTotal(valorTotal)
                .fechaCreacion(Instant.now())
                .platos(platos)
                .build();
        return pedidoRepositoryPort.guardar(pedido);
    }
}

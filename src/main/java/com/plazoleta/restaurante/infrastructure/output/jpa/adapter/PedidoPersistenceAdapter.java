package com.plazoleta.restaurante.infrastructure.output.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;
import com.plazoleta.restaurante.domain.model.PedidoPlato;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PedidoEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PedidoPlatoEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.PedidoJpaRepository;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.PedidoPlatoJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoPersistenceAdapter implements PedidoRepositoryPort {

    private static final List<EstadoPedido> ESTADOS_ACTIVOS =
            List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO);

    private final PedidoJpaRepository pedidoRepository;
    private final PedidoPlatoJpaRepository pedidoPlatoRepository;

    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoEntity entidad = pedidoRepository.save(PedidoEntity.builder()
                .id(pedido.getId())
                .idCliente(pedido.getIdCliente())
                .idRestaurante(pedido.getIdRestaurante())
                .estado(pedido.getEstado())
                .idEmpleadoAsignado(pedido.getIdEmpleadoAsignado())
                .valorTotal(pedido.getValorTotal())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaFinalizacion(pedido.getFechaFinalizacion())
                .codigoPinHash(pedido.getCodigoPinHash())
                .build());

        List<PedidoPlato> platos = pedido.getPlatos() == null ? List.of() : pedido.getPlatos();
        if (pedido.getId() == null) {
            pedidoPlatoRepository.saveAll(platos.stream()
                    .map(plato -> PedidoPlatoEntity.builder()
                            .idPedido(entidad.getId())
                            .idPlato(plato.idPlato())
                            .nombrePlato(plato.nombrePlato())
                            .precioUnitario(plato.precioUnitario())
                            .cantidad(plato.cantidad())
                            .build())
                    .toList());
        }
        return mapear(entidad, platos);
    }

    @Override
    public boolean existePedidoActivoPorCliente(Long idCliente) {
        return pedidoRepository.existsByIdClienteAndEstadoIn(idCliente, ESTADOS_ACTIVOS);
    }

    @Override
    public Optional<Pedido> obtenerPorId(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .map(entidad -> mapear(
                        entidad,
                        pedidoPlatoRepository.findAllByIdPedidoOrderByIdAsc(idPedido).stream()
                                .map(this::mapearPlato)
                                .toList()));
    }

    private Pedido mapear(PedidoEntity entidad, List<PedidoPlato> platos) {
        return Pedido.builder()
                .id(entidad.getId())
                .idCliente(entidad.getIdCliente())
                .idRestaurante(entidad.getIdRestaurante())
                .estado(entidad.getEstado())
                .idEmpleadoAsignado(entidad.getIdEmpleadoAsignado())
                .valorTotal(entidad.getValorTotal())
                .fechaCreacion(entidad.getFechaCreacion())
                .fechaFinalizacion(entidad.getFechaFinalizacion())
                .codigoPinHash(entidad.getCodigoPinHash())
                .platos(platos)
                .build();
    }

    private PedidoPlato mapearPlato(PedidoPlatoEntity entidad) {
        return new PedidoPlato(
                entidad.getIdPlato(),
                entidad.getNombrePlato(),
                entidad.getPrecioUnitario(),
                entidad.getCantidad());
    }
}

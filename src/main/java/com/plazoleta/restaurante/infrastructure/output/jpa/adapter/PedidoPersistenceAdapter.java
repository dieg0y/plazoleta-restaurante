package com.plazoleta.restaurante.infrastructure.output.jpa.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pagina;
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
    private final EntityManager entityManager;

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

    @Override
    public Pagina<Pedido> listarPorRestauranteYEstado(
            Long idRestaurante, EstadoPedido estado, int pagina, int tamano) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PedidoEntity> consulta = criteriaBuilder.createQuery(PedidoEntity.class);
        Root<PedidoEntity> pedido = consulta.from(PedidoEntity.class);
        consulta.select(pedido)
                .where(predicados(criteriaBuilder, pedido, idRestaurante, estado))
                .orderBy(criteriaBuilder.asc(pedido.get("fechaCreacion")), criteriaBuilder.asc(pedido.get("id")));
        List<PedidoEntity> entidades = entityManager.createQuery(consulta)
                .setFirstResult(Math.toIntExact((long) pagina * tamano))
                .setMaxResults(tamano)
                .getResultList();

        CriteriaQuery<Long> consultaConteo = criteriaBuilder.createQuery(Long.class);
        Root<PedidoEntity> pedidoConteo = consultaConteo.from(PedidoEntity.class);
        consultaConteo.select(criteriaBuilder.count(pedidoConteo))
                .where(predicados(criteriaBuilder, pedidoConteo, idRestaurante, estado));
        long totalElementos = entityManager.createQuery(consultaConteo).getSingleResult();

        List<Long> idsPedidos = entidades.stream().map(PedidoEntity::getId).toList();
        Map<Long, List<PedidoPlato>> platosPorPedido = idsPedidos.isEmpty()
                ? Map.of()
                : pedidoPlatoRepository.findAllByIdPedidoInOrderByIdAsc(idsPedidos).stream()
                        .collect(Collectors.groupingBy(
                                PedidoPlatoEntity::getIdPedido,
                                Collectors.mapping(this::mapearPlato, Collectors.toList())));
        List<Pedido> pedidos = entidades.stream()
                .map(entidad -> mapear(entidad, platosPorPedido.getOrDefault(entidad.getId(), List.of())))
                .toList();
        return new Pagina<>(pedidos, pagina, tamano, totalElementos);
    }

    private Predicate[] predicados(
            CriteriaBuilder criteriaBuilder, Root<PedidoEntity> pedido,
            Long idRestaurante, EstadoPedido estado) {
        return new Predicate[] {
                criteriaBuilder.equal(pedido.get("idRestaurante"), idRestaurante),
                criteriaBuilder.equal(pedido.get("estado"), estado)
        };
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

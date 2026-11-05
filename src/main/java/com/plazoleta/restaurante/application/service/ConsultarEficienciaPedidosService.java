package com.plazoleta.restaurante.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import com.plazoleta.restaurante.application.port.input.ConsultarEficienciaPedidosUseCase;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.DuracionPedido;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.InformeEficienciaPedidos;
import com.plazoleta.restaurante.domain.model.Pedido;
import com.plazoleta.restaurante.domain.model.PromedioTiempoEmpleado;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarEficienciaPedidosService implements ConsultarEficienciaPedidosUseCase {

    private static final BigDecimal MILISEGUNDOS_POR_SEGUNDO = BigDecimal.valueOf(1_000);

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final RestauranteRepositoryPort restauranteRepositoryPort;

    @Override
    public InformeEficienciaPedidos consultar(Long idRestaurante, Long idPropietario) {
        if (idRestaurante == null || idRestaurante <= 0) {
            throw new BusinessException("El restaurante es obligatorio");
        }
        var restaurante = restauranteRepositoryPort.obtenerPorId(idRestaurante)
                .orElseThrow(() -> new BusinessException("El restaurante no existe"));
        if (!Objects.equals(restaurante.getIdPropietario(), idPropietario)) {
            throw new BusinessException("No puedes consultar la eficiencia de un restaurante que no te pertenece");
        }

        List<Pedido> pedidosEntregados = pedidoRepositoryPort.listarEntregadosPorRestaurante(idRestaurante);
        List<DuracionPedido> duraciones = new ArrayList<>();
        Map<Long, List<Long>> duracionesPorEmpleado = new HashMap<>();
        for (Pedido pedido : pedidosEntregados) {
            Duration duracion = Duration.between(pedido.getFechaCreacion(), pedido.getFechaFinalizacion());
            if (duracion.isNegative()) {
                throw new BusinessException("El pedido tiene fechas de inicio y finalización inválidas");
            }
            long duracionMilisegundos = duracion.toMillis();
            duraciones.add(new DuracionPedido(
                    pedido.getId(),
                    pedido.getIdEmpleadoAsignado(),
                    BigDecimal.valueOf(duracionMilisegundos)
                            .divide(MILISEGUNDOS_POR_SEGUNDO, 3, RoundingMode.HALF_UP)));
            if (pedido.getIdEmpleadoAsignado() != null) {
                duracionesPorEmpleado.computeIfAbsent(pedido.getIdEmpleadoAsignado(), ignorado -> new ArrayList<>())
                        .add(duracionMilisegundos);
            }
        }

        List<PromedioTiempoEmpleado> promediosOrdenados = duracionesPorEmpleado.entrySet().stream()
                .map(entrada -> new PromedioTiempoEmpleado(
                        entrada.getKey(),
                        entrada.getValue().size(),
                        promedioSegundos(entrada.getValue()),
                        0))
                .sorted(Comparator.comparing(PromedioTiempoEmpleado::promedioDuracionSegundos)
                        .thenComparing(PromedioTiempoEmpleado::idEmpleado))
                .toList();
        List<PromedioTiempoEmpleado> ranking = IntStream.range(0, promediosOrdenados.size())
                .mapToObj(posicion -> {
                    PromedioTiempoEmpleado promedio = promediosOrdenados.get(posicion);
                    return new PromedioTiempoEmpleado(
                            promedio.idEmpleado(),
                            promedio.pedidosEntregados(),
                            promedio.promedioDuracionSegundos(),
                            posicion + 1);
                })
                .toList();

        return new InformeEficienciaPedidos(duraciones, ranking);
    }

    private BigDecimal promedioSegundos(List<Long> duracionesMilisegundos) {
        BigDecimal totalMilisegundos = duracionesMilisegundos.stream()
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalMilisegundos
                .divide(BigDecimal.valueOf(duracionesMilisegundos.size()).multiply(MILISEGUNDOS_POR_SEGUNDO),
                        2, RoundingMode.HALF_UP);
    }
}

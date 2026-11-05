package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.ListarPedidosUseCase;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarPedidosService implements ListarPedidosUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;
    private final PedidoRepositoryPort pedidoRepositoryPort;

    @Override
    public Pagina<Pedido> listar(Long idUsuarioEmpleado, EstadoPedido estado, int pagina, int tamano) {
        if (idUsuarioEmpleado == null || idUsuarioEmpleado <= 0) {
            throw new BusinessException("El empleado autenticado no es válido");
        }
        if (estado == null) {
            throw new BusinessException("El estado del pedido es obligatorio");
        }
        if (pagina < 0) {
            throw new BusinessException("El número de página no puede ser negativo");
        }
        if (tamano <= 0) {
            throw new BusinessException("El tamaño de página debe ser mayor a 0");
        }
        if ((long) pagina * tamano > Integer.MAX_VALUE) {
            throw new BusinessException("El desplazamiento de página es demasiado grande");
        }

        var empleado = empleadoRepositoryPort.obtenerPorIdUsuario(idUsuarioEmpleado)
                .orElseThrow(() -> new BusinessException("El empleado no está asociado a un restaurante"));
        return pedidoRepositoryPort.listarPorRestauranteYEstado(
                empleado.getIdRestaurante(), estado, pagina, tamano);
    }
}

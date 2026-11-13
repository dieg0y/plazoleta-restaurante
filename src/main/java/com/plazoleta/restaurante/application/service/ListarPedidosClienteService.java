package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.ListarPedidosClienteUseCase;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarPedidosClienteService implements ListarPedidosClienteUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;

    @Override
    public Pagina<Pedido> listar(Long idCliente, Long idPedido, int pagina, int tamano) {
        if (idCliente == null || idCliente <= 0) {
            throw new BusinessException("El cliente autenticado no es válido");
        }
        if (idPedido != null && idPedido <= 0) {
            throw new BusinessException("El id del pedido debe ser mayor a 0");
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

        return pedidoRepositoryPort.listarPorCliente(idCliente, idPedido, pagina, tamano);
    }
}

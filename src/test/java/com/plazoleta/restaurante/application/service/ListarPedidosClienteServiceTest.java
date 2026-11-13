package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Pedido;

@ExtendWith(MockitoExtension.class)
class ListarPedidosClienteServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private ListarPedidosClienteService service;

    @Test
    void listaSoloLosPedidosDelClienteConElFiltroOpcional() {
        when(pedidoRepositoryPort.listarPorCliente(14L, 9L, 1, 5))
                .thenReturn(new Pagina<>(List.<Pedido>of(), 1, 5, 0));

        service.listar(14L, 9L, 1, 5);

        verify(pedidoRepositoryPort).listarPorCliente(14L, 9L, 1, 5);
    }

    @Test
    void rechazaIdentificadoresYPaginacionInvalidos() {
        assertThrows(BusinessException.class, () -> service.listar(0L, null, 0, 10));
        assertThrows(BusinessException.class, () -> service.listar(14L, 0L, 0, 10));
        assertThrows(BusinessException.class, () -> service.listar(14L, null, -1, 10));
        assertThrows(BusinessException.class, () -> service.listar(14L, null, 0, 0));
    }
}

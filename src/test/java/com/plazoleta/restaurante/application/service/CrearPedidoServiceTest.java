package com.plazoleta.restaurante.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.restaurante.application.port.input.CrearPedidoCommand;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.EstadoPedido;
import com.plazoleta.restaurante.domain.model.Pedido;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;

@ExtendWith(MockitoExtension.class)
class CrearPedidoServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private PlatoRepositoryPort platoRepositoryPort;

    @Mock
    private RestauranteRepositoryPort restauranteRepositoryPort;

    @InjectMocks
    private CrearPedidoService service;

    @Test
    void creaPedidoPendienteConElPrecioVigenteYElTotalCalculado() {
        when(pedidoRepositoryPort.existePedidoActivoPorCliente(14L)).thenReturn(false);
        when(restauranteRepositoryPort.obtenerPorId(3L)).thenReturn(Optional.of(restaurante(3L)));
        when(platoRepositoryPort.obtenerPorId(7L)).thenReturn(Optional.of(plato(7L, 3L, "Bandeja", "12000")));
        when(platoRepositoryPort.obtenerPorId(8L)).thenReturn(Optional.of(plato(8L, 3L, "Jugo", "4000")));
        when(pedidoRepositoryPort.guardar(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido pedido = service.crear(14L, new CrearPedidoCommand(3L, List.of(
                new CrearPedidoCommand.PlatoSolicitado(7L, 2),
                new CrearPedidoCommand.PlatoSolicitado(8L, 3))));

        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(0, new BigDecimal("36000").compareTo(pedido.getValorTotal()));
        assertEquals(2, pedido.getPlatos().size());
        assertEquals(7L, pedido.getPlatos().get(0).idPlato());
        verify(pedidoRepositoryPort).guardar(any(Pedido.class));
    }

    @Test
    void rechazaSegundoPedidoMientrasElClienteTieneUnoActivo() {
        when(pedidoRepositoryPort.existePedidoActivoPorCliente(14L)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> service.crear(14L, new CrearPedidoCommand(3L, List.of(
                        new CrearPedidoCommand.PlatoSolicitado(7L, 1)))));

        verify(restauranteRepositoryPort, never()).obtenerPorId(any());
        verify(pedidoRepositoryPort, never()).guardar(any());
    }

    @Test
    void rechazaPlatosDeOtroRestaurante() {
        when(pedidoRepositoryPort.existePedidoActivoPorCliente(14L)).thenReturn(false);
        when(restauranteRepositoryPort.obtenerPorId(3L)).thenReturn(Optional.of(restaurante(3L)));
        when(platoRepositoryPort.obtenerPorId(9L)).thenReturn(Optional.of(plato(9L, 4L, "Sushi", "25000")));

        assertThrows(BusinessException.class,
                () -> service.crear(14L, new CrearPedidoCommand(3L, List.of(
                        new CrearPedidoCommand.PlatoSolicitado(9L, 1)))));

        verify(pedidoRepositoryPort, never()).guardar(any());
    }

    @Test
    void rechazaPlatoInactivoYPlatosDuplicados() {
        when(pedidoRepositoryPort.existePedidoActivoPorCliente(14L)).thenReturn(false);
        when(restauranteRepositoryPort.obtenerPorId(3L)).thenReturn(Optional.of(restaurante(3L)));
        when(platoRepositoryPort.obtenerPorId(7L))
                .thenReturn(Optional.of(plato(7L, 3L, "Bandeja", "12000")))
                .thenReturn(Optional.of(plato(7L, 3L, "Bandeja", "12000", false)));

        assertThrows(BusinessException.class,
                () -> service.crear(14L, new CrearPedidoCommand(3L, List.of(
                        new CrearPedidoCommand.PlatoSolicitado(7L, 1),
                        new CrearPedidoCommand.PlatoSolicitado(7L, 1)))));
        assertThrows(BusinessException.class,
                () -> service.crear(14L, new CrearPedidoCommand(3L, List.of(
                        new CrearPedidoCommand.PlatoSolicitado(7L, 1)))));

        verify(pedidoRepositoryPort, never()).guardar(any());
    }

    private Restaurante restaurante(Long id) {
        return Restaurante.builder().id(id).idPropietario(5L).build();
    }

    private Plato plato(Long id, Long idRestaurante, String nombre, String precio) {
        return plato(id, idRestaurante, nombre, precio, true);
    }

    private Plato plato(Long id, Long idRestaurante, String nombre, String precio, boolean activo) {
        return Plato.builder()
                .id(id)
                .nombre(nombre)
                .categoria(CategoriaPlato.PLATO_FUERTE)
                .precio(new BigDecimal(precio))
                .descripcion("Descripcion")
                .urlImagen("https://example.com/plato.png")
                .idRestaurante(idRestaurante)
                .activo(activo)
                .build();
    }
}

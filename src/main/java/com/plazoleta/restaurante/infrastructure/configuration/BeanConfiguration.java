package com.plazoleta.restaurante.infrastructure.configuration;

import com.plazoleta.restaurante.application.port.input.CambiarEstadoPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.AsignarPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.CancelarPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.ConsultarTrazabilidadPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearRestauranteUseCase;
import com.plazoleta.restaurante.application.port.input.EntregarPedidoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPlatosUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPedidosUseCase;
import com.plazoleta.restaurante.application.port.input.MarcarPedidoListoUseCase;
import com.plazoleta.restaurante.application.port.input.ListarRestaurantesUseCase;
import com.plazoleta.restaurante.application.port.input.ModificarPlatoUseCase;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PedidoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.CodigoSeguridadPedidoPort;
import com.plazoleta.restaurante.application.port.output.EnviarSmsPort;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.application.service.CambiarEstadoPlatoService;
import com.plazoleta.restaurante.application.service.AsignarPedidoService;
import com.plazoleta.restaurante.application.service.CancelarPedidoService;
import com.plazoleta.restaurante.application.service.ConsultarTrazabilidadPedidoService;
import com.plazoleta.restaurante.application.service.CrearEmpleadoService;
import com.plazoleta.restaurante.application.service.CrearPedidoService;
import com.plazoleta.restaurante.application.service.CrearPlatoService;
import com.plazoleta.restaurante.application.service.CrearRestauranteService;
import com.plazoleta.restaurante.application.service.EntregarPedidoService;
import com.plazoleta.restaurante.application.service.ListarPlatosService;
import com.plazoleta.restaurante.application.service.ListarPedidosService;
import com.plazoleta.restaurante.application.service.MarcarPedidoListoService;
import com.plazoleta.restaurante.application.service.ListarRestaurantesService;
import com.plazoleta.restaurante.application.service.ModificarPlatoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CrearPedidoUseCase crearPedidoUseCase(
            PedidoRepositoryPort pedidoRepositoryPort,
            PlatoRepositoryPort platoRepositoryPort,
            RestauranteRepositoryPort restauranteRepositoryPort) {
        return new CrearPedidoService(pedidoRepositoryPort, platoRepositoryPort, restauranteRepositoryPort);
    }

    @Bean
    public ListarPedidosUseCase listarPedidosUseCase(
            EmpleadoRepositoryPort empleadoRepositoryPort,
            PedidoRepositoryPort pedidoRepositoryPort) {
        return new ListarPedidosService(empleadoRepositoryPort, pedidoRepositoryPort);
    }

    @Bean
    public AsignarPedidoUseCase asignarPedidoUseCase(
            EmpleadoRepositoryPort empleadoRepositoryPort,
            PedidoRepositoryPort pedidoRepositoryPort) {
        return new AsignarPedidoService(empleadoRepositoryPort, pedidoRepositoryPort);
    }

    @Bean
    public MarcarPedidoListoUseCase marcarPedidoListoUseCase(
            EmpleadoRepositoryPort empleadoRepositoryPort,
            PedidoRepositoryPort pedidoRepositoryPort,
            UsuarioClientePort usuarioClientePort,
            CodigoSeguridadPedidoPort codigoSeguridadPedidoPort,
            EnviarSmsPort enviarSmsPort) {
        return new MarcarPedidoListoService(
                empleadoRepositoryPort, pedidoRepositoryPort, usuarioClientePort,
                codigoSeguridadPedidoPort, enviarSmsPort);
    }

    @Bean
    public EntregarPedidoUseCase entregarPedidoUseCase(
            EmpleadoRepositoryPort empleadoRepositoryPort,
            PedidoRepositoryPort pedidoRepositoryPort,
            CodigoSeguridadPedidoPort codigoSeguridadPedidoPort) {
        return new EntregarPedidoService(
                empleadoRepositoryPort, pedidoRepositoryPort, codigoSeguridadPedidoPort);
    }

    @Bean
    public CancelarPedidoUseCase cancelarPedidoUseCase(PedidoRepositoryPort pedidoRepositoryPort) {
        return new CancelarPedidoService(pedidoRepositoryPort);
    }

    @Bean
    public ConsultarTrazabilidadPedidoUseCase consultarTrazabilidadPedidoUseCase(
            PedidoRepositoryPort pedidoRepositoryPort) {
        return new ConsultarTrazabilidadPedidoService(pedidoRepositoryPort);
    }

    @Bean
    public CrearRestauranteUseCase crearRestauranteUseCase(
            RestauranteRepositoryPort restauranteRepositoryPort,
            ValidarPropietarioPort validarPropietarioPort) {
        return new CrearRestauranteService(restauranteRepositoryPort, validarPropietarioPort);
    }

    @Bean
    public CrearPlatoUseCase crearPlatoUseCase(
            PlatoRepositoryPort platoRepositoryPort,
            RestauranteRepositoryPort restauranteRepositoryPort,
            ValidarPropietarioPort validarPropietarioPort) {
        return new CrearPlatoService(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Bean
    public CrearEmpleadoUseCase crearEmpleadoUseCase(
            RestauranteRepositoryPort restauranteRepositoryPort,
            ValidarPropietarioPort validarPropietarioPort,
            UsuarioClientePort usuarioClientePort,
            EmpleadoRepositoryPort empleadoRepositoryPort) {
        return new CrearEmpleadoService(restauranteRepositoryPort, validarPropietarioPort,
                usuarioClientePort, empleadoRepositoryPort);
    }

    @Bean
    public ListarPlatosUseCase listarPlatosUseCase(
            PlatoRepositoryPort platoRepositoryPort,
            RestauranteRepositoryPort restauranteRepositoryPort) {
        return new ListarPlatosService(platoRepositoryPort, restauranteRepositoryPort);
    }

    @Bean
    public ModificarPlatoUseCase modificarPlatoUseCase(
            PlatoRepositoryPort platoRepositoryPort,
            RestauranteRepositoryPort restauranteRepositoryPort,
            ValidarPropietarioPort validarPropietarioPort) {
        return new ModificarPlatoService(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Bean
    public CambiarEstadoPlatoUseCase cambiarEstadoPlatoUseCase(
            PlatoRepositoryPort platoRepositoryPort,
            RestauranteRepositoryPort restauranteRepositoryPort,
            ValidarPropietarioPort validarPropietarioPort) {
        return new CambiarEstadoPlatoService(platoRepositoryPort, restauranteRepositoryPort, validarPropietarioPort);
    }

    @Bean
    public ListarRestaurantesUseCase listarRestaurantesUseCase(
            RestauranteRepositoryPort restauranteRepositoryPort) {
        return new ListarRestaurantesService(restauranteRepositoryPort);
    }
}
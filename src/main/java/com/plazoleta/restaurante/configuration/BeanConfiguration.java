package com.plazoleta.restaurante.configuration;

import com.plazoleta.restaurante.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearRestauranteUseCase;
import com.plazoleta.restaurante.application.port.input.ListarPlatosUseCase;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.application.service.CrearEmpleadoService;
import com.plazoleta.restaurante.application.service.CrearPlatoService;
import com.plazoleta.restaurante.application.service.CrearRestauranteService;
import com.plazoleta.restaurante.application.service.ListarPlatosService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

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
}
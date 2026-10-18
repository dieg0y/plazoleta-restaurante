package com.plazoleta.restaurante.configuration;

import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.input.CrearRestauranteUseCase;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.application.service.CrearPlatoService;
import com.plazoleta.restaurante.application.service.CrearRestauranteService;
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
}
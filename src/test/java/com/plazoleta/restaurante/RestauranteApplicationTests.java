package com.plazoleta.restaurante;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.plazoleta.restaurante.infrastructure.output.jpa.repository.PlatoJpaRepository;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.RestauranteJpaRepository;

@SpringBootTest
class RestauranteApplicationTests {

	@Autowired
	private RestauranteJpaRepository restauranteRepository;

	@Autowired
	private PlatoJpaRepository platoRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void seedsRestaurantsAndEightPlatesWithExpectedAvailability() {
		assertThat(restauranteRepository.count()).isEqualTo(2);
		assertThat(platoRepository.count()).isEqualTo(8);
		assertThat(platoRepository.findAll().stream().filter(plato -> Boolean.TRUE.equals(plato.getActivo())).count())
				.isEqualTo(6L);
		assertThat(platoRepository.findAll().stream().filter(plato -> Boolean.FALSE.equals(plato.getActivo())).count())
				.isEqualTo(2L);
	}
}

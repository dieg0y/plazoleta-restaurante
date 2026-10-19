package com.plazoleta.restaurante.infrastructure.configuration;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PlatoEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.RestauranteEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.PlatoJpaRepository;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.RestauranteJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RestauranteJpaRepository restauranteRepository;
    private final PlatoJpaRepository platoRepository;

    @Value("${app.seed.juan-owner-id:2}")
    private Long juanOwnerId;

    @Value("${app.seed.maria-owner-id:3}")
    private Long mariaOwnerId;

    @Override
    @Transactional
    public void run(String... args) {
        if (restauranteRepository.count() > 0 || platoRepository.count() > 0) {
            return;
        }

        List<RestauranteEntity> restaurantes = restauranteRepository.saveAll(List.of(
                RestauranteEntity.builder()
                        .nombre("Sabor Paisa")
                        .direccion("Calle 10 # 20-30")
                        .telefono("+573000000101")
                        .urlLogo("https://example.com/sabor-paisa.png")
                        .nit("900123456")
                        .idPropietario(juanOwnerId)
                        .build(),
                RestauranteEntity.builder()
                        .nombre("Sushi Go")
                        .direccion("Carrera 15 # 80-20")
                        .telefono("+573000000102")
                        .urlLogo("https://example.com/sushi-go.png")
                        .nit("900654321")
                        .idPropietario(mariaOwnerId)
                        .build()));

        RestauranteEntity saborPaisa = restaurantes.get(0);
        RestauranteEntity sushiGo = restaurantes.get(1);

        platoRepository.saveAll(List.of(
                plato("Bandeja Paisa", CategoriaPlato.PLATO_FUERTE,
                        "Bandeja tradicional con carne, arroz, frijoles y huevo.", 38000, saborPaisa, true),
                plato("Arepa de Choclo", CategoriaPlato.ENTRADA,
                        "Arepa de maiz dulce con queso.", 9000, saborPaisa, true),
                plato("Limonada Natural", CategoriaPlato.BEBIDA,
                        "Limonada preparada al momento.", 6000, saborPaisa, true),
                plato("Arroz Paisa", CategoriaPlato.PLATO_FUERTE,
                        "Arroz salteado con ingredientes de la casa.", 24000, saborPaisa, true),
                plato("Sushi Salmon", CategoriaPlato.PLATO_FUERTE,
                        "Roll de salmon fresco, arroz y aguacate.", 32000, sushiGo, true),
                plato("Sopa Miso", CategoriaPlato.ENTRADA,
                        "Sopa japonesa de miso y tofu.", 11000, sushiGo, true),
                plato("Mochi", CategoriaPlato.POSTRE,
                        "Postre japones de arroz glutinoso.", 7000, sushiGo, false),
                plato("Te Verde", CategoriaPlato.BEBIDA,
                        "Te verde japones servido frio.", 3000, sushiGo, false)));
    }

    private PlatoEntity plato(String nombre, CategoriaPlato categoria, String descripcion, int precio,
            RestauranteEntity restaurante, boolean activo) {
        return PlatoEntity.builder()
                .nombre(nombre)
                .categoria(categoria)
                .descripcion(descripcion)
                .precio(BigDecimal.valueOf(precio))
                .urlImagen("https://example.com/platos/" + nombre.toLowerCase().replace(' ', '-') + ".png")
                .idRestaurante(restaurante.getId())
                .activo(activo)
                .build();
    }
}

package com.plazoleta.restaurante.infrastructure.out.jpa.adapter;

import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.out.jpa.entity.RestauranteEntity;
import com.plazoleta.restaurante.infrastructure.out.jpa.mapper.RestaurantePersistenceMapper;
import com.plazoleta.restaurante.infrastructure.out.jpa.repository.RestauranteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantePersistenceAdapter implements RestauranteRepositoryPort {

    private final RestauranteJpaRepository jpaRepository;
    private final RestaurantePersistenceMapper mapper;

    @Override
    public Restaurante guardar(Restaurante restaurante) {
        RestauranteEntity entity = mapper.toEntity(restaurante);
        RestauranteEntity guardada = jpaRepository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public Optional<Restaurante> obtenerPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
package com.plazoleta.restaurante.infrastructure.out.jpa.adapter;

import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.out.jpa.entity.PlatoEntity;
import com.plazoleta.restaurante.infrastructure.out.jpa.mapper.PlatoPersistenceMapper;
import com.plazoleta.restaurante.infrastructure.out.jpa.repository.PlatoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlatoPersistenceAdapter implements PlatoRepositoryPort {

    private final PlatoJpaRepository jpaRepository;
    private final PlatoPersistenceMapper mapper;

    @Override
    public Plato guardar(Plato plato) {
        PlatoEntity entity = mapper.toEntity(plato);
        PlatoEntity guardado = jpaRepository.save(entity);
        return mapper.toDomain(guardado);
    }
}
package com.plazoleta.restaurante.infrastructure.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.PaginaPlatos;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.out.jpa.entity.PlatoEntity;
import com.plazoleta.restaurante.infrastructure.out.jpa.mapper.PlatoPersistenceMapper;
import com.plazoleta.restaurante.infrastructure.out.jpa.repository.PlatoJpaRepository;

import lombok.RequiredArgsConstructor;

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

    @Override
    public Optional<Plato> obtenerPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PaginaPlatos listarPorRestaurante(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano);

        Page<PlatoEntity> paginaEntities = (categoria == null)
                ? jpaRepository.findByIdRestauranteAndActivoTrue(idRestaurante, pageable)
                : jpaRepository.findByIdRestauranteAndCategoriaAndActivoTrue(idRestaurante, categoria, pageable);

        List<Plato> platos = paginaEntities.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PaginaPlatos(platos, pagina, tamano, paginaEntities.getTotalElements());
    }
}
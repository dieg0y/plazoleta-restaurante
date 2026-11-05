package com.plazoleta.restaurante.infrastructure.output.jpa.adapter;

import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Restaurante;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.RestauranteEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.mapper.RestaurantePersistenceMapper;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.RestauranteJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantePersistenceAdapter implements RestauranteRepositoryPort {

    private final RestauranteJpaRepository jpaRepository;
    private final RestaurantePersistenceMapper mapper;
    private final EntityManager entityManager;

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

    // HU9: orden alfabetico por nombre, resuelto por la base de datos
    @Override
    public Pagina<Restaurante> listarOrdenadosPorNombre(int pagina, int tamano) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RestauranteEntity> consulta = criteriaBuilder.createQuery(RestauranteEntity.class);
        Root<RestauranteEntity> restaurante = consulta.from(RestauranteEntity.class);
        consulta.select(restaurante)
                .orderBy(criteriaBuilder.asc(restaurante.get("nombre")), criteriaBuilder.asc(restaurante.get("id")));

        List<RestauranteEntity> entidades = entityManager.createQuery(consulta)
                .setFirstResult(Math.toIntExact((long) pagina * tamano))
                .setMaxResults(tamano)
                .getResultList();

        CriteriaQuery<Long> consultaConteo = criteriaBuilder.createQuery(Long.class);
        consultaConteo.select(criteriaBuilder.count(consultaConteo.from(RestauranteEntity.class)));
        long totalElementos = entityManager.createQuery(consultaConteo).getSingleResult();

        List<Restaurante> restaurantes = entidades
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new Pagina<>(restaurantes, pagina, tamano, totalElementos);
    }
}
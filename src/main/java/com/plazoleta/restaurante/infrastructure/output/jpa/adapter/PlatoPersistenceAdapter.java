package com.plazoleta.restaurante.infrastructure.output.jpa.adapter;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import com.plazoleta.restaurante.domain.model.Pagina;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.infrastructure.output.jpa.entity.PlatoEntity;
import com.plazoleta.restaurante.infrastructure.output.jpa.mapper.PlatoPersistenceMapper;
import com.plazoleta.restaurante.infrastructure.output.jpa.repository.PlatoJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlatoPersistenceAdapter implements PlatoRepositoryPort {

    private final PlatoJpaRepository jpaRepository;
    private final PlatoPersistenceMapper mapper;
    private final EntityManager entityManager;

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
    public Pagina<Plato> listarPorRestaurante(Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano) {
        return listar(idRestaurante, categoria, pagina, tamano, true);
    }

    @Override
    public Pagina<Plato> listarTodosPorRestaurante(
            Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano) {
        return listar(idRestaurante, categoria, pagina, tamano, false);
    }

    private Pagina<Plato> listar(
            Long idRestaurante, CategoriaPlato categoria, int pagina, int tamano, boolean soloActivos) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PlatoEntity> consulta = criteriaBuilder.createQuery(PlatoEntity.class);
        Root<PlatoEntity> plato = consulta.from(PlatoEntity.class);
        consulta.select(plato).where(predicados(criteriaBuilder, plato, idRestaurante, categoria, soloActivos))
                .orderBy(criteriaBuilder.asc(plato.get("nombre")), criteriaBuilder.asc(plato.get("id")));

        List<PlatoEntity> entidades = entityManager.createQuery(consulta)
                .setFirstResult(Math.toIntExact((long) pagina * tamano))
                .setMaxResults(tamano)
                .getResultList();

        CriteriaQuery<Long> consultaConteo = criteriaBuilder.createQuery(Long.class);
        Root<PlatoEntity> platoConteo = consultaConteo.from(PlatoEntity.class);
        consultaConteo.select(criteriaBuilder.count(platoConteo))
                .where(predicados(criteriaBuilder, platoConteo, idRestaurante, categoria, soloActivos));
        long totalElementos = entityManager.createQuery(consultaConteo).getSingleResult();

        List<Plato> platos = entidades.stream()
                .map(mapper::toDomain)
                .toList();

        return new Pagina<>(platos, pagina, tamano, totalElementos);
    }

    private Predicate[] predicados(
            CriteriaBuilder criteriaBuilder, Root<PlatoEntity> plato,
            Long idRestaurante, CategoriaPlato categoria, boolean soloActivos) {
        List<Predicate> predicados = new java.util.ArrayList<>();
        predicados.add(criteriaBuilder.equal(plato.get("idRestaurante"), idRestaurante));
        if (soloActivos) {
            predicados.add(criteriaBuilder.isTrue(plato.get("activo")));
        }
        if (categoria != null) {
            predicados.add(criteriaBuilder.equal(plato.get("categoria"), categoria));
        }
        return predicados.toArray(Predicate[]::new);
    }
}
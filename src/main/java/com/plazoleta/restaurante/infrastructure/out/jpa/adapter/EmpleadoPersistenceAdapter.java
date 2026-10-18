package com.plazoleta.restaurante.infrastructure.out.jpa.adapter;

import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.infrastructure.out.jpa.entity.EmpleadoEntity;
import com.plazoleta.restaurante.infrastructure.out.jpa.repository.EmpleadoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ADAPTADOR DE SALIDA: persiste la asociación empleado-restaurante.
 * El mapeo es manual porque la entidad solo guarda 3 campos
 * (el resto del modelo son datos transitorios para el Feign).
 */
@Component
@RequiredArgsConstructor
public class EmpleadoPersistenceAdapter implements EmpleadoRepositoryPort {

    private final EmpleadoJpaRepository jpaRepository;

    @Override
    public Empleado guardar(Empleado empleado) {
        EmpleadoEntity entity = EmpleadoEntity.builder()
                .idUsuario(empleado.getIdUsuario())
                .idRestaurante(empleado.getIdRestaurante())
                .build();

        EmpleadoEntity guardado = jpaRepository.save(entity);

        return Empleado.builder()
                .id(guardado.getId())
                .idUsuario(guardado.getIdUsuario())
                .idRestaurante(guardado.getIdRestaurante())
                .build();
    }
}
package com.plazoleta.restaurante.infrastructure.out.feign.dto;

import lombok.Data;

/** Respuesta del microservicio usuario: solo nos interesa el id creado. */
@Data
public class EmpleadoRemotoResponse {

    private Long id;
}
package com.plazoleta.restaurante.infrastructure.out.feign.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo que se envía al microservicio usuario para crear la cuenta. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoRemotoRequest {

    private String nombre;
    private String apellido;
    private String documentoIdentidad;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
}
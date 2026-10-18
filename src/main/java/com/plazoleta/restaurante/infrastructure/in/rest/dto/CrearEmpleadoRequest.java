package com.plazoleta.restaurante.infrastructure.in.rest.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearEmpleadoRequest {

    private Long idRestaurante;
    private String nombre;
    private String apellido;
    private String documentoIdentidad;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
}
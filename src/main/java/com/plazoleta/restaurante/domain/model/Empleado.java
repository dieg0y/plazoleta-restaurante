package com.plazoleta.restaurante.domain.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HU4: asociación entre la cuenta de un empleado (vive en el
 * microservicio usuario) y el restaurante donde trabaja.
 * Los datos personales solo viajan para REENVIARLOS al
 * microservicio usuario, donde se crea la cuenta con rol EMPLEADO.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {

    /** Id de la asociación empleado-restaurante (BD de este microservicio). */
    private Long id;

    /** Id de la cuenta creada en el microservicio usuario. */
    private Long idUsuario;

    /** Restaurante donde trabaja el empleado. */
    private Long idRestaurante;

    // ===== Datos de la cuenta: se reenvían al microservicio usuario =====
    private String nombre;
    private String apellido;
    private String documentoIdentidad;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
}
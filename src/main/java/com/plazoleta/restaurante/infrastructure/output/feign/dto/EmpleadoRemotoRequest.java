package com.plazoleta.restaurante.infrastructure.output.feign.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo que se envía al microservicio usuario para crear la cuenta. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos enviados al microservicio usuarios para crear un empleado")
public class EmpleadoRemotoRequest {

    @Schema(description = "Nombre")
    private String nombre;
    @Schema(description = "Apellido")
    private String apellido;
    @Schema(description = "Documento de identidad")
    private String documentoIdentidad;
    @Schema(description = "Celular")
    private String celular;
    @Schema(description = "Fecha de nacimiento")
    private LocalDate fechaNacimiento;
    @Schema(description = "Correo electrónico")
    private String correo;
    @Schema(description = "Clave en texto plano, que el servicio usuarios almacena con BCrypt")
    private String clave;
}
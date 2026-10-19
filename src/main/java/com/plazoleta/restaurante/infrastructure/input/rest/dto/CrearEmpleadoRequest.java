package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una cuenta de empleado (HU6)")
public class CrearEmpleadoRequest {

    @Schema(description = "Restaurante donde trabajara el empleado", example = "1")
    private Long idRestaurante;
    @Schema(description = "Nombre", example = "Ana")
    private String nombre;
    @Schema(description = "Apellido", example = "Gomez")
    private String apellido;
    @Schema(description = "Documento numerico", example = "1000000004")
    private String documentoIdentidad;
    @Schema(description = "Celular de hasta 13 caracteres", example = "+573000000004")
    private String celular;
    @Schema(description = "Fecha de nacimiento", example = "1990-01-01")
    private LocalDate fechaNacimiento;
    @Schema(description = "Correo electronico", example = "ana@plazoleta.com")
    private String correo;
    @Schema(description = "Clave, almacenada con BCrypt", example = "Empleado123*")
    private String clave;
}
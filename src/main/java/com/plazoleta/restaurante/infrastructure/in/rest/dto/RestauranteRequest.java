package com.plazoleta.restaurante.infrastructure.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteRequest {

    private String nombre;
    private String direccion;
    private String telefono;
    private String urlLogo;
    private String nit;
    private Long idPropietario;
}
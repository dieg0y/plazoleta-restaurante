package com.plazoleta.restaurante.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurante {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String urlLogo;
    private String nit;
    private Long idPropietario;
}
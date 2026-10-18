package com.plazoleta.restaurante.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {

    private Long id;
    private String nombre;
    private CategoriaPlato categoria;
    private String descripcion;
    private BigDecimal precio;
    private String urlImagen;
    private Long idRestaurante;
    private Boolean activo;
}
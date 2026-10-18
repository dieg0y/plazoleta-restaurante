package com.plazoleta.restaurante.infrastructure.in.rest.dto;

import com.plazoleta.restaurante.domain.model.CategoriaPlato;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatoResponse {

    private Long id;
    private String nombre;
    private CategoriaPlato categoria;
    private String descripcion;
    private BigDecimal precio;
    private String urlImagen;
    private Long idRestaurante;
    private Boolean activo;
}
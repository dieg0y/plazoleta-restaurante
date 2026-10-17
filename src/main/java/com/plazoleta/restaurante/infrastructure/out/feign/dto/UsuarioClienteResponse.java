package com.plazoleta.restaurante.infrastructure.out.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioClienteResponse {

    private Long id;
    private String rol;
}
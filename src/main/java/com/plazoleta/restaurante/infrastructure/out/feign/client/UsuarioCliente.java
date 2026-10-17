package com.plazoleta.restaurante.infrastructure.out.feign.client;

import com.plazoleta.restaurante.infrastructure.out.feign.dto.UsuarioClienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuario-client", url = "http://localhost:8080")
public interface UsuarioCliente {

    @GetMapping("/usuarios/{id}")
    UsuarioClienteResponse obtenerUsuario(@PathVariable("id") Long id);
}
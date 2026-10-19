package com.plazoleta.restaurante.infrastructure.output.feign.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.plazoleta.restaurante.infrastructure.output.feign.dto.EmpleadoRemotoRequest;
import com.plazoleta.restaurante.infrastructure.output.feign.dto.EmpleadoRemotoResponse;
import com.plazoleta.restaurante.infrastructure.output.feign.dto.UsuarioClienteResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuario-client", url = "${app.usuario.url:http://localhost:8080}")
public interface UsuarioCliente {

    @GetMapping("/usuarios/{id}")
    UsuarioClienteResponse obtenerUsuario(@PathVariable("id") Long id);

    @PostMapping("/empleados")
    EmpleadoRemotoResponse crearEmpleado(@RequestBody EmpleadoRemotoRequest request);
}
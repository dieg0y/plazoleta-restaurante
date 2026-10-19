package com.plazoleta.restaurante.infrastructure.output.feign.adapter;

import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.infrastructure.output.feign.client.UsuarioCliente;
import com.plazoleta.restaurante.infrastructure.output.feign.dto.EmpleadoRemotoRequest;
import com.plazoleta.restaurante.infrastructure.output.feign.dto.EmpleadoRemotoResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ADAPTADOR DE SALIDA: implementa el puerto UsuarioClientePort
 * usando el cliente Feign hacia el microservicio usuario.
 */
@Component
@RequiredArgsConstructor
public class UsuarioClienteAdapter implements UsuarioClientePort {

    private final UsuarioCliente usuarioCliente;

    @Override
    public Long crearCuentaEmpleado(Empleado empleado) {
        EmpleadoRemotoRequest request = EmpleadoRemotoRequest.builder()
                .nombre(empleado.getNombre())
                .apellido(empleado.getApellido())
                .documentoIdentidad(empleado.getDocumentoIdentidad())
                .celular(empleado.getCelular())
                .fechaNacimiento(empleado.getFechaNacimiento())
                .correo(empleado.getCorreo())
                .clave(empleado.getClave())
                .build();

        try {
            EmpleadoRemotoResponse respuesta = usuarioCliente.crearEmpleado(request);
            return respuesta.getId();
        } catch (FeignException e) {
            // El microservicio usuario rechazó (ej: correo duplicado):
            // lo propagamos como error de negocio, no como 500.
            throw new BusinessException(
                    "Error al crear la cuenta del empleado: " + e.contentUTF8());
        }
    }
}
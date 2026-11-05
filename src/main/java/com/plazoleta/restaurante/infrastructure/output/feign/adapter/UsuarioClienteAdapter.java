package com.plazoleta.restaurante.infrastructure.output.feign.adapter;

import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.infrastructure.output.feign.client.UsuarioCliente;
import com.plazoleta.restaurante.infrastructure.output.feign.dto.EmpleadoRemotoRequest;
import com.plazoleta.restaurante.infrastructure.output.feign.dto.EmpleadoRemotoResponse;
import com.plazoleta.restaurante.infrastructure.output.feign.dto.CelularClienteRemotoResponse;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ADAPTADOR DE SALIDA: implementa el puerto UsuarioClientePort
 * usando el cliente Feign hacia el microservicio usuario.
 */
@Component
public class UsuarioClienteAdapter implements UsuarioClientePort {

    private final UsuarioCliente usuarioCliente;
    private final String internalToken;

    public UsuarioClienteAdapter(
            UsuarioCliente usuarioCliente,
            @Value("${app.internal.token:}") String internalToken) {
        this.usuarioCliente = usuarioCliente;
        this.internalToken = internalToken;
    }

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

    @Override
    public String obtenerCelularCliente(Long idCliente) {
        if (internalToken == null || internalToken.length() < 32) {
            throw new IllegalStateException("APP_INTERNAL_TOKEN debe tener al menos 32 caracteres");
        }
        try {
            CelularClienteRemotoResponse respuesta =
                    usuarioCliente.obtenerCelularCliente(idCliente, internalToken);
            if (respuesta == null || respuesta.celular() == null || respuesta.celular().isBlank()) {
                throw new BusinessException("El cliente no tiene un teléfono de contacto válido");
            }
            return respuesta.celular();
        } catch (FeignException e) {
            throw new BusinessException(
                    "No se pudo obtener el teléfono del cliente (HTTP " + e.status() + ")");
        }
    }
}
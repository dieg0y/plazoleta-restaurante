package com.plazoleta.restaurante.infrastructure.out.feign.adapter;

import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.infrastructure.out.feign.client.UsuarioCliente;
import com.plazoleta.restaurante.infrastructure.out.feign.dto.UsuarioClienteResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidarPropietarioAdapter implements ValidarPropietarioPort {

    private static final String ROL_PROPIETARIO = "PROPIETARIO";

    private final UsuarioCliente usuarioCliente;

    @Override
    public void validarPropietario(Long id) {
        UsuarioClienteResponse usuario;
        try {
            usuario = usuarioCliente.obtenerUsuario(id);
        } catch (FeignException excepcion) {
            throw new BusinessException("El usuario con id " + id + " no existe");
        }

        if (!ROL_PROPIETARIO.equals(usuario.getRol())) {
            throw new BusinessException("El usuario con id " + id + " no tiene el rol de propietario");
        }
    }
}
package com.plazoleta.restaurante.application.port.output;

public interface ValidarPropietarioPort {

    /**
     * Verifica que el id corresponda a un usuario existente con rol PROPIETARIO.
     * @throws com.plazoleta.restaurante.domain.exception.BusinessException si no existe o el rol no es PROPIETARIO
     */
    void validarPropietario(Long idPropietario);
}
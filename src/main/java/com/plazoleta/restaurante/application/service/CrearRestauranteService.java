package com.plazoleta.restaurante.application.service;

import lombok.RequiredArgsConstructor;

import com.plazoleta.restaurante.application.port.input.CrearRestauranteUseCase;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Restaurante;

@RequiredArgsConstructor
public class CrearRestauranteService implements CrearRestauranteUseCase {

    private final RestauranteRepositoryPort restauranteRepositoryPort;
    private final ValidarPropietarioPort validarPropietarioPort;

    @Override
    public Restaurante crear(Restaurante restaurante) {

        if (restaurante.getNombre() == null || restaurante.getNombre().isBlank()) {
            throw new BusinessException("El nombre del restaurante es obligatorio");
        }
        if (restaurante.getNombre().matches("\\d+")) {
            throw new BusinessException("El nombre del restaurante no puede estar compuesto unicamente por numeros");
        }
        if (restaurante.getDireccion() == null || restaurante.getDireccion().isBlank()) {
            throw new BusinessException("La dirección del restaurante es obligatoria");
        }
        if (restaurante.getTelefono() == null || !restaurante.getTelefono().matches("^\\+?\\d+$")
                || restaurante.getTelefono().length() > 13) {
            throw new BusinessException("El teléfono debe ser numérico y de máximo 13 caracteres");
        }
        if (restaurante.getUrlLogo() == null || restaurante.getUrlLogo().isBlank()) {
            throw new BusinessException("La url del logo es obligatoria");
        }
        if (restaurante.getNit() == null || !restaurante.getNit().matches("^\\d+$")) {
            throw new BusinessException("El NIT debe ser numérico");
        }
        if (restaurante.getIdPropietario() == null || restaurante.getIdPropietario() <= 0) {
            throw new BusinessException("El id del propietario es obligatorio");
        }

        validarPropietarioPort.validarPropietario(restaurante.getIdPropietario());

        return restauranteRepositoryPort.guardar(restaurante);
    }
}
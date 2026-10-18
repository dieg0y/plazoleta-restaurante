package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.CrearPlatoUseCase;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CrearPlatoService implements CrearPlatoUseCase {

    private final PlatoRepositoryPort platoRepositoryPort;
    private final RestauranteRepositoryPort restauranteRepositoryPort;
    private final ValidarPropietarioPort validarPropietarioPort;

    @Override
    public Plato crear(Plato plato, Long idPropietario) {
        validarCampos(plato, idPropietario);

        Restaurante restaurante = restauranteRepositoryPort.obtenerPorId(plato.getIdRestaurante())
                .orElseThrow(() -> new BusinessException(
                        "El restaurante con id " + plato.getIdRestaurante() + " no existe"));

        if (!restaurante.getIdPropietario().equals(idPropietario)) {
            throw new BusinessException("Solo el propietario del restaurante puede crear platos");
        }

        validarPropietarioPort.validarPropietario(idPropietario);

        plato.setActivo(true);

        return platoRepositoryPort.guardar(plato);
    }

    private void validarCampos(Plato plato, Long idPropietario) {
        if (plato.getNombre() == null || plato.getNombre().isBlank()) {
            throw new BusinessException("El nombre del plato es obligatorio");
        }
        if (plato.getCategoria() == null) {
            throw new BusinessException("La categoria del plato es obligatoria");
        }
        if (plato.getPrecio() == null || plato.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio del plato debe ser un entero positivo mayor a 0");
        }
        if (plato.getPrecio().stripTrailingZeros().scale() > 0) {
            throw new BusinessException("El precio del plato debe ser un entero positivo mayor a 0");
        }
        if (plato.getDescripcion() == null || plato.getDescripcion().isBlank()) {
            throw new BusinessException("La descripcion del plato es obligatoria");
        }
        if (plato.getUrlImagen() == null || plato.getUrlImagen().isBlank()) {
            throw new BusinessException("La url de la imagen del plato es obligatoria");
        }
        if (plato.getIdRestaurante() == null || plato.getIdRestaurante() <= 0) {
            throw new BusinessException("El id del restaurante es obligatorio");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new BusinessException("El id del propietario es obligatorio");
        }
    }
}
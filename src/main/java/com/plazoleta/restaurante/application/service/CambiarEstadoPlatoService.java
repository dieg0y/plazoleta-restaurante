package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.CambiarEstadoPlatoUseCase;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;
import lombok.RequiredArgsConstructor;

/**
 * CASO DE USO HU7: habilitar o deshabilitar un plato.
 *
 * Reglas del enunciado:
 *  1. Solo se modifica el estado (activo/deshabilitado), ningún otro campo.
 *  2. Solo el propietario dueño del restaurante asociado al plato.
 */
@RequiredArgsConstructor
public class CambiarEstadoPlatoService implements CambiarEstadoPlatoUseCase {

    private final PlatoRepositoryPort platoRepositoryPort;
    private final RestauranteRepositoryPort restauranteRepositoryPort;
    private final ValidarPropietarioPort validarPropietarioPort;

    @Override
    public Plato cambiarEstado(Long idPlato, Boolean activo, Long idPropietario) {
        validarParametros(idPlato, activo, idPropietario);

        Plato plato = platoRepositoryPort.obtenerPorId(idPlato)
                .orElseThrow(() -> new BusinessException(
                        "El plato con id " + idPlato + " no existe"));

        Restaurante restaurante = restauranteRepositoryPort.obtenerPorId(plato.getIdRestaurante())
                .orElseThrow(() -> new BusinessException(
                        "El restaurante con id " + plato.getIdRestaurante() + " no existe"));

        if (!restaurante.getIdPropietario().equals(idPropietario)) {
            throw new BusinessException("Solo el propietario del restaurante puede cambiar el estado de los platos");
        }

        validarPropietarioPort.validarPropietario(idPropietario);

        // HU7: SOLO se toca el estado del plato, ningún otro campo.
        plato.setActivo(activo);

        return platoRepositoryPort.guardar(plato);
    }

    private void validarParametros(Long idPlato, Boolean activo, Long idPropietario) {
        if (idPlato == null || idPlato <= 0) {
            throw new BusinessException("El id del plato es obligatorio");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new BusinessException("El id del propietario es obligatorio");
        }
        if (activo == null) {
            throw new BusinessException("El estado del plato (activo) es obligatorio");
        }
    }
}
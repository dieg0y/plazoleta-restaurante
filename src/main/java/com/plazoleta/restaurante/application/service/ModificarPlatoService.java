package com.plazoleta.restaurante.application.service;

import java.math.BigDecimal;

import com.plazoleta.restaurante.application.port.input.ModificarPlatoUseCase;
import com.plazoleta.restaurante.application.port.output.PlatoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Plato;
import com.plazoleta.restaurante.domain.model.Restaurante;
import lombok.RequiredArgsConstructor;

/**
 * CASO DE USO HU4: actualizar la información de un plato.
 *
 * Reglas del enunciado:
 *  1. Solo se puede modificar precio y descripción.
 *  2. El precio debe ser entero positivo mayor a 0.
 *  3. Solo el propietario dueño del restaurante asociado al plato.
 */
@RequiredArgsConstructor
public class ModificarPlatoService implements ModificarPlatoUseCase {

    private final PlatoRepositoryPort platoRepositoryPort;
    private final RestauranteRepositoryPort restauranteRepositoryPort;
    private final ValidarPropietarioPort validarPropietarioPort;

    @Override
    public Plato modificar(Long idPlato, BigDecimal precio, String descripcion, Long idPropietario) {
        validarParametros(idPlato, precio, descripcion, idPropietario);

        Plato plato = platoRepositoryPort.obtenerPorId(idPlato)
                .orElseThrow(() -> new BusinessException(
                        "El plato con id " + idPlato + " no existe"));

        Restaurante restaurante = restauranteRepositoryPort.obtenerPorId(plato.getIdRestaurante())
                .orElseThrow(() -> new BusinessException(
                        "El restaurante con id " + plato.getIdRestaurante() + " no existe"));

        if (!restaurante.getIdPropietario().equals(idPropietario)) {
            throw new BusinessException("Solo el propietario del restaurante puede modificar platos");
        }

        validarPropietarioPort.validarPropietario(idPropietario);

        // HU4: SOLO se tocan precio y descripción, ningún otro campo.
        plato.setPrecio(precio);
        plato.setDescripcion(descripcion);

        return platoRepositoryPort.guardar(plato);
    }

    private void validarParametros(Long idPlato, BigDecimal precio, String descripcion, Long idPropietario) {
        if (idPlato == null || idPlato <= 0) {
            throw new BusinessException("El id del plato es obligatorio");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new BusinessException("El id del propietario es obligatorio");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio del plato debe ser un entero positivo mayor a 0");
        }
        if (precio.stripTrailingZeros().scale() > 0) {
            throw new BusinessException("El precio del plato debe ser un entero positivo mayor a 0");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new BusinessException("La descripcion del plato es obligatoria");
        }
    }
}
package com.plazoleta.restaurante.application.service;

import com.plazoleta.restaurante.application.port.input.CrearEmpleadoUseCase;
import com.plazoleta.restaurante.application.port.output.EmpleadoRepositoryPort;
import com.plazoleta.restaurante.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.restaurante.application.port.output.UsuarioClientePort;
import com.plazoleta.restaurante.application.port.output.ValidarPropietarioPort;
import com.plazoleta.restaurante.domain.exception.BusinessException;
import com.plazoleta.restaurante.domain.model.Empleado;
import com.plazoleta.restaurante.domain.model.Restaurante;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrearEmpleadoService implements CrearEmpleadoUseCase {

    private final RestauranteRepositoryPort restauranteRepositoryPort;
    private final ValidarPropietarioPort validarPropietarioPort;
    private final UsuarioClientePort usuarioClientePort;
    private final EmpleadoRepositoryPort empleadoRepositoryPort;

    @Override
    public Empleado crearEmpleado(Empleado empleado, Long idPropietario) {
        validarCampos(empleado, idPropietario);

        Restaurante restaurante = restauranteRepositoryPort.obtenerPorId(empleado.getIdRestaurante())
                .orElseThrow(() -> new BusinessException(
                        "El restaurante con id " + empleado.getIdRestaurante() + " no existe"));

        if (!restaurante.getIdPropietario().equals(idPropietario)) {
            throw new BusinessException("Solo el propietario del restaurante puede crear empleados");
        }

        validarPropietarioPort.validarPropietario(idPropietario);

        Long idUsuario = usuarioClientePort.crearCuentaEmpleado(empleado);

        return empleadoRepositoryPort.guardar(Empleado.builder()
                .idUsuario(idUsuario)
                .idRestaurante(empleado.getIdRestaurante())
                .build());
    }

    private void validarCampos(Empleado empleado, Long idPropietario) {
        if (empleado == null) {
            throw new BusinessException("El cuerpo de la petición es obligatorio");
        }
        if (empleado.getIdRestaurante() == null || empleado.getIdRestaurante() <= 0) {
            throw new BusinessException("El id del restaurante es obligatorio");
        }
        if (idPropietario == null || idPropietario <= 0) {
            throw new BusinessException("El id del propietario es obligatorio");
        }
        if (empleado.getNombre() == null || empleado.getNombre().isBlank()) {
            throw new BusinessException("El nombre del empleado es obligatorio");
        }
        if (empleado.getApellido() == null || empleado.getApellido().isBlank()) {
            throw new BusinessException("El apellido del empleado es obligatorio");
        }
        if (empleado.getDocumentoIdentidad() == null || empleado.getDocumentoIdentidad().isBlank()) {
            throw new BusinessException("El documento del empleado es obligatorio");
        }
        if (empleado.getCelular() == null || empleado.getCelular().isBlank()) {
            throw new BusinessException("El celular del empleado es obligatorio");
        }
        if (empleado.getFechaNacimiento() == null) {
            throw new BusinessException("La fecha de nacimiento del empleado es obligatoria");
        }
        if (empleado.getCorreo() == null || empleado.getCorreo().isBlank()) {
            throw new BusinessException("El correo del empleado es obligatorio");
        }
        if (empleado.getClave() == null || empleado.getClave().isBlank()) {
            throw new BusinessException("La clave del empleado es obligatoria");
        }
    }
}
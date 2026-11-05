package com.plazoleta.restaurante.domain.model;

import java.util.List;
import java.util.function.Function;

public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos) {

    public Pagina {
        contenido = List.copyOf(contenido);
    }

    public int totalPaginas() {
        return tamano <= 0 ? 0 : Math.toIntExact((totalElementos + tamano - 1) / tamano);
    }

    public <R> Pagina<R> map(Function<? super T, R> mapper) {
        return new Pagina<>(contenido.stream().map(mapper).toList(), pagina, tamano, totalElementos);
    }
}

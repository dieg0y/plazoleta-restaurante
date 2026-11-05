package com.plazoleta.restaurante.infrastructure.input.rest.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.restaurante.domain.model.Pagina;

class PageResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializaElContratoGenericoConMetadatosDePaginacion() throws Exception {
        PageResponse<Integer> response = PageResponse.from(new Pagina<>(List.of(11, 12), 1, 10, 12));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.path("contenido").toString()).isEqualTo("[11,12]");
        assertThat(json.path("pagina").asInt()).isEqualTo(1);
        assertThat(json.path("tamano").asInt()).isEqualTo(10);
        assertThat(json.path("totalElementos").asLong()).isEqualTo(12);
        assertThat(json.path("totalPaginas").asInt()).isEqualTo(2);
    }
}

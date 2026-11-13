package com.plazoleta.restaurante.infrastructure.input.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantePropietarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.jwt.secret}")
    private String secreto;

    @Test
    void requiereAutenticacionParaListarRestaurantesPropios() throws Exception {
        mockMvc.perform(get("/restaurantes/mio"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaUsuariosQueNoSeanPropietarios() throws Exception {
        mockMvc.perform(get("/restaurantes/mio")
                .header(HttpHeaders.AUTHORIZATION, bearerToken(1L, "ADMINISTRADOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void devuelveSoloRestaurantesDelPropietarioAutenticado() throws Exception {
        mockMvc.perform(get("/restaurantes/mio")
                .header(HttpHeaders.AUTHORIZATION, bearerToken(2L, "PROPIETARIO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Sabor Paisa")))
                .andExpect(jsonPath("$[0].idPropietario", is(2)));
    }

    private String bearerToken(Long id, String rol) {
        Instant ahora = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject(String.valueOf(id))
                .claim("role", rol)
                .claim("correo", "usuario@plazoleta.com")
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(300)))
                .signWith(key)
                .compact();
        return "Bearer " + token;
    }
}

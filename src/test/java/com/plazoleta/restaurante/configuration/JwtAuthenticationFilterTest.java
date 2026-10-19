package com.plazoleta.restaurante.infrastructure.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.plazoleta.restaurante.infrastructure.configuration.JwtAuthenticationFilter;
import com.plazoleta.restaurante.infrastructure.configuration.UsuarioAutenticado;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtAuthenticationFilterTest {

    private static final String SECRETO = "secreto-de-prueba-bien-largo-para-hs256-1234567890";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8));

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private String tokenValido(Long id, String correo, String rol) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("role", rol)
                .claim("correo", correo)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(3600000)))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("Token válido de propietario deja al usuario autenticado con su rol")
    void tokenValidoAutenticaUsuario() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION,
                "Bearer " + tokenValido(5L, "prop@test.com", "PROPIETARIO"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();

        new JwtAuthenticationFilter(SECRETO).doFilter(request, response, cadena);

        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(autenticacion);
        UsuarioAutenticado usuario = (UsuarioAutenticado) autenticacion.getPrincipal();
        assertEquals(5L, usuario.id());
        assertEquals("prop@test.com", usuario.correo());
        assertEquals("PROPIETARIO", usuario.rol());
        assertTrue(autenticacion.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROPIETARIO")));
    }

    @Test
    @DisplayName("Request sin header Authorization continúa sin autenticación")
    void sinHeaderNoAutentica() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();

        new JwtAuthenticationFilter(SECRETO).doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Token firmado con otro secreto (falsificado) continúa sin autenticación")
    void tokenInvalidoNoAutentica() throws Exception {
        SecretKey otroSecreto = Keys.hmacShaKeyFor(
                "otro-secreto-totalmente-distinto-1234567890123456".getBytes(StandardCharsets.UTF_8));
        String tokenFalsificado = Jwts.builder()
                .subject("1")
                .claim("role", "ADMINISTRADOR")
                .expiration(Date.from(Instant.now().plusMillis(3600000)))
                .signWith(otroSecreto)
                .compact();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFalsificado);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();

        new JwtAuthenticationFilter(SECRETO).doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
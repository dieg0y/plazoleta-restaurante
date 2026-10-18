package com.plazoleta.restaurante.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * HU5 (parte restaurante): protege los endpoints por rol.
 *
 *  - POST /restaurantes  -> solo ADMINISTRADOR (el dueno se asigna en el body)
 *  - POST /platos, PUT/PATCH /platos/**, POST /empleados -> solo PROPIETARIO
 *  - GET /restaurantes (HU9, listado publico), GET /platos/** (menu),
 *    Swagger y /error -> publicos
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            @Value("${app.jwt.secret}") String secreto) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Publico: Swagger, errores, menu y listado de restaurantes (HU9)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/platos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurantes").permitAll()
                // HU5: proteccion por rol
                .requestMatchers(HttpMethod.POST, "/restaurantes").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/platos").hasRole("PROPIETARIO")
                .requestMatchers(HttpMethod.PUT, "/platos/**").hasRole("PROPIETARIO")
                .requestMatchers(HttpMethod.PATCH, "/platos/**").hasRole("PROPIETARIO")
                .requestMatchers(HttpMethod.POST, "/empleados").hasRole("PROPIETARIO")
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(secreto),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
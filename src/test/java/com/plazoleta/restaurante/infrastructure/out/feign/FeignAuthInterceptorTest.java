package com.plazoleta.restaurante.infrastructure.out.feign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestTemplate;

class FeignAuthInterceptorTest {

    private final FeignAuthInterceptor interceptor = new FeignAuthInterceptor();

    @AfterEach
    void limpiar() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Reenvía el header Authorization del request original al request Feign")
    void reenviaToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer eyJ-token-de-prueba");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertTrue(template.headers().containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals("Bearer eyJ-token-de-prueba",
                template.headers().get(HttpHeaders.AUTHORIZATION).iterator().next());
    }

    @Test
    @DisplayName("Sin request activo no agrega el header al request Feign")
    void sinRequestNoAgrega() {
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey(HttpHeaders.AUTHORIZATION));
    }
}
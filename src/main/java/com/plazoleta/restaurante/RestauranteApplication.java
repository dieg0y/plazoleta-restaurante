package com.plazoleta.restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RestauranteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestauranteApplication.class, args);
    }
}
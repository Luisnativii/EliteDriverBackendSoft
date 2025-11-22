package com.example.elitedriverbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/*
    Configuración de CORS para permitir solicitudes desde dominios específicos.
    Permite métodos HTTP comunes y todas las cabeceras.
    Aplica la configuración a las rutas que comienzan con /api/.
 */
@Configuration
public class CorsConfig {

    /*
        Define un filtro CORS con configuraciones específicas.
        Permite orígenes específicos, métodos HTTP, cabeceras y credenciales.
        Registra la configuración para rutas que comienzan con /api/.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://elite-driver-soft.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        System.out.println("✅ CORS Config aplicada exitosamente ✅");

        return new CorsFilter(source);
    }
}

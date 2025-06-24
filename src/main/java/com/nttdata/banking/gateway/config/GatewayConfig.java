package com.nttdata.banking.gateway.config;

import com.nttdata.banking.gateway.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Lazy
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ms-client", r -> r.path("/api/clients/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(createConfig(List.of("/api/clients/public")))))
                        .uri("http://localhost:8080"))
                // Agregar más rutas según sea necesario
                .build();
    }

    private JwtAuthenticationFilter.Config createConfig(List<String> publicEndpoints) {
        JwtAuthenticationFilter.Config config = new JwtAuthenticationFilter.Config();
        config.setPublicEndpoints(publicEndpoints);
        return config;
    }
}
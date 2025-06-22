package com.nttdata.banking.gateway.config;

import com.nttdata.banking.gateway.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/authentication/users/register",
            "/api/v1/authentication/users/login",
            "/api/v1/authentication/users/refresh-token",
            "/api/v1/authentication/users/logout"
    );

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("service-client", r -> r.path("/api/clients/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8080/"))
                .route("authservice", r -> r.path("/api/v1/authentication/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:1112/"))
                .route("ms-loan", r -> r.path("/api/loans/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8092/"))
                .route("ms-movement", r -> r.path("/api/movements/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8083/"))
                .route("ms-credits", r -> r.path("/api/credits/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8084/"))
                .route("ms-bank-account", r -> r.path("/api/bankaccounts/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8085/"))
                .route("debit-card", r -> r.path("/api/debitcard/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8086/"))
                .route("ms-person-type", r -> r.path("/api/persontype/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8087/"))
                .route("ms-account-type", r -> r.path("/api/accounttype/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8091/"))
                .route("ms-currency-type", r -> r.path("/api/currencytype/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://localhost:8089/"))
                .build();
    }
}
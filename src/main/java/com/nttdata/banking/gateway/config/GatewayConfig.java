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
                // Microservicios existentes
                .route("service-client", r -> r.path("/api/clients/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-client:8080/"))
                .route("authservice", r -> r.path("/api/v1/authentication/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-auth:1112/"))
                .route("ms-loan", r -> r.path("/api/loans/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-loan:8093/")) // Corregido puerto
                .route("ms-movement", r -> r.path("/api/movements/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-movement:8083/"))
                .route("ms-credits", r -> r.path("/api/credits/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-credit:8084/"))
                .route("ms-bank-account", r -> r.path("/api/bankaccounts/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-bank-account:8085/"))
                .route("debit-card", r -> r.path("/api/debitcard/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-debit-card:8086/"))
                .route("ms-mobile-wallet", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-mobile-wallet:8090/"))
                .route("ms-exchange-rate", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-exchange-rate:8095/"))
                .route("ms-bootcoin", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-bootcoin:8096/"))
                .route("ms-bootcoin-movement", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-bootcoin-movement:8097/"))
                .route("ms-bootcoin-purchase-request", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()
                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                        .uri("http://ms-bootcoin-purchase-request:8098/"))
                .build();
    }
}
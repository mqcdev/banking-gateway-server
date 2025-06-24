package com.nttdata.banking.gateway.config;

import com.nttdata.banking.gateway.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ============================================
                // RUTAS DE AUTENTICACIÓN (SIN JWT FILTER)
                // ============================================
                .route("auth", r -> r.path("/api/auth/**")
                        .filters(f -> f.removeRequestHeader("Host"))
                        .uri("http://ms-auth:1112"))

                // ============================================
                // SERVICIOS CORE DE NEGOCIO (CON JWT FILTER)
                // ============================================

                // Microservicio de Clientes - Puerto 8080
                .route("ms-client", r -> r.path("/api/clients/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/clients/public",
                                        "/api/clients/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-client:8080"))

                // Microservicio de Cuentas Bancarias - Puerto 8085
                .route("ms-bank-account", r -> r.path("/api/accounts/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/accounts/public",
                                        "/api/accounts/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-bank-account:8085"))

                // Microservicio de Créditos - Puerto 8084
                .route("ms-credit", r -> r.path("/api/credits/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/credits/public",
                                        "/api/credits/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-credit:8084"))

                // Microservicio de Préstamos - Puerto 8093
                .route("ms-loan", r -> r.path("/api/loans/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/loans/public",
                                        "/api/loans/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-loan:8093"))

                // Microservicio de Movimientos - Puerto 8083
                .route("ms-movement", r -> r.path("/api/movements/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/movements/public",
                                        "/api/movements/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-movement:8083"))

                // ============================================
                // SERVICIOS ADICIONALES (CON JWT FILTER)
                // ============================================

                // Microservicio de Tarjetas de Débito - Puerto 8086
                .route("ms-debit-card", r -> r.path("/api/debitcard/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/debitcard/public",
                                        "/api/debitcard/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-debit-card:8086"))

                // ============================================
                // SERVICIOS BOOTCOIN Y YANKI (CON JWT FILTER)
                // ============================================

                // Microservicio de Billetera Móvil (Yanki) - Puerto 8090
                .route("ms-mobile-wallet", r -> r.path("/api/yanki/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/yanki/public",
                                        "/api/yanki/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-mobile-wallet:8090"))

                // Microservicio de BootCoin - Puerto 8096
                .route("ms-bootcoin", r -> r.path("/api/bootcoin/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/bootcoin/public",
                                        "/api/bootcoin/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-bootcoin:8096"))

                // Microservicio de Solicitudes de Compra BootCoin - Puerto 8098
                .route("ms-bootcoin-purchase-request", r -> r.path("/api/bootcoin-requests/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/bootcoin-requests/public",
                                        "/api/bootcoin-requests/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-bootcoin-purchase-request:8098"))

                // Microservicio de Tipo de Cambio - Puerto 8095
                .route("ms-exchange-rate", r -> r.path("/api/exchange-rate/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/exchange-rate/public",
                                        "/api/exchange-rate/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-exchange-rate:8095"))

                // Microservicio de Movimientos BootCoin - Puerto 8097
                .route("ms-bootcoin-movement", r -> r.path("/api/bootcoin-movements/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(createConfig(List.of(
                                        "/api/bootcoin-movements/public",
                                        "/api/bootcoin-movements/health"
                                ))))
                                .removeRequestHeader("Host")
                        )
                        .uri("http://ms-bootcoin-movement:8097"))

                .build();
    }

    /**
     * Crea la configuración para el filtro JWT con endpoints públicos
     * @param publicEndpoints Lista de endpoints que no requieren autenticación
     * @return Configuración del filtro
     */
    private JwtAuthenticationFilter.Config createConfig(List<String> publicEndpoints) {
        JwtAuthenticationFilter.Config config = new JwtAuthenticationFilter.Config();
        config.setPublicEndpoints(publicEndpoints);
        return config;
    }

    /**
     * Configuración para servicios que no requieren autenticación JWT
     * @return Configuración vacía
     */
    private JwtAuthenticationFilter.Config createPublicConfig() {
        return new JwtAuthenticationFilter.Config();
    }
}
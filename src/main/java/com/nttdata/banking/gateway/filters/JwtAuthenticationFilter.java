package com.nttdata.banking.gateway.filters;

import com.nttdata.banking.gateway.service.TokenValidationService;
import com.nttdata.banking.gateway.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final TokenValidationService tokenValidationService;

    public JwtAuthenticationFilter(TokenValidationService tokenValidationService) {
        super(Config.class);
        this.tokenValidationService = tokenValidationService;
    }

    public static class Config {
        private List<String> publicEndpoints;

        public List<String> getPublicEndpoints() {
            return publicEndpoints;
        }

        public Config setPublicEndpoints(List<String> publicEndpoints) {
            this.publicEndpoints = publicEndpoints;
            return this;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            logger.info("Processing request for path: {}", path);

            // Verificar si la ruta es pública
            if (config != null && config.getPublicEndpoints() != null &&
                    config.getPublicEndpoints().stream().anyMatch(path::startsWith)) {
                logger.info("Public endpoint detected, skipping authentication for path: {}", path);
                return chain.filter(exchange);
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            logger.info("Authorization header: {}", authorizationHeader != null ? "Present" : "Missing");

            if (TokenUtils.isBearerToken(authorizationHeader)) {
                String jwt = TokenUtils.getJwt(authorizationHeader);
                logger.info("JWT token extracted, validating...");

                return tokenValidationService.validateToken(jwt)
                        .flatMap(isValid -> {
                            if (isValid) {
                                logger.info("Token validation successful for path: {}", path);
                                return chain.filter(exchange);
                            } else {
                                logger.warn("Token validation failed for path: {}", path);
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().setComplete();
                            }
                        })
                        .onErrorResume(e -> {
                            logger.error("Token validation error for path: {}", path, e);
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            }

            logger.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }
}
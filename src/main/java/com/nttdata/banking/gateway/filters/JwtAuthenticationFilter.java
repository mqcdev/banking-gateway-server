package com.nttdata.banking.gateway.filters;

import com.nttdata.banking.gateway.model.Token;
import com.nttdata.banking.gateway.service.InvalidTokenService;
import com.nttdata.banking.gateway.service.TokenService;
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

    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        super(Config.class);
        this.tokenService = tokenService;
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

            if (config != null && config.getPublicEndpoints().stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (Token.isBearerToken(authorizationHeader)) {
                String jwt = Token.getJwt(authorizationHeader);

                // Solo verificamos que el token sea válido sintácticamente
                return tokenService.verifyAndValidate(jwt)
                        .flatMap(valid -> {
                            logger.debug("Token validation succeeded for path: {}", path);
                            return chain.filter(exchange);
                        })
                        .onErrorResume(e -> {
                            logger.error("Token validation failed for path: {}", path, e);
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
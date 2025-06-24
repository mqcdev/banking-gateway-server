package com.nttdata.banking.gateway.filters;

import com.nttdata.banking.gateway.model.Token;
import com.nttdata.banking.gateway.service.TokenValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TokenInvalidityFilter extends AbstractGatewayFilterFactory<Object> {
    private static final Logger logger = LoggerFactory.getLogger(TokenInvalidityFilter.class);

    private final TokenValidationService tokenValidationService;

    public TokenInvalidityFilter(TokenValidationService tokenValidationService) {
        super(Object.class);
        this.tokenValidationService = tokenValidationService;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (!Token.isBearerToken(authorizationHeader)) {
                return chain.filter(exchange);
            }

            String jwt = Token.getJwt(authorizationHeader);
            String path = exchange.getRequest().getURI().getPath();

            return tokenValidationService.checkTokenValidity(jwt)
                    .flatMap(valid -> {
                        logger.debug("Token invalidity check passed for path: {}", path);
                        return chain.filter(exchange);
                    })
                    .onErrorResume(e -> {
                        logger.error("Token is invalid or has been revoked: {}", path, e);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        }, 1); // Orden 1 (se ejecuta después del JwtAuthenticationFilter)
    }
}
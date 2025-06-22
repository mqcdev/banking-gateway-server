package com.nttdata.banking.gateway.filters;

import com.nttdata.banking.gateway.client.UserServiceClient;
import com.nttdata.banking.gateway.model.Token;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter() {
        super(Config.class);
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

                ApplicationContext context = exchange.getApplicationContext();
                UserServiceClient userServiceClient = context.getBean(UserServiceClient.class);

                return Mono.fromCallable(() -> {
                            userServiceClient.validateToken(jwt);
                            logger.debug("Token validation succeeded for path: {}", path);
                            return true;
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(valid -> chain.filter(exchange))
                        .onErrorResume(e -> {
                            logger.error("Token validation failed for path: {}", path, e);
                            if (e instanceof FeignException.Unauthorized || e instanceof FeignException.Forbidden) {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            } else {
                                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                            return exchange.getResponse().setComplete();
                        });
            }
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            return chain.filter(exchange);
        };
    }
}
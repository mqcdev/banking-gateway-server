package com.nttdata.banking.gateway.client;

import com.nttdata.banking.gateway.model.Client;
import com.nttdata.banking.gateway.model.InvalidTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/clients").build();
    }

    public Mono<Boolean> existsUserEntityByEmail(String email) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/exists-by-email").queryParam("email", email).build())
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Client> findUserEntityByEmail(String email) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/find-by-email").queryParam("email", email).build())
                .retrieve()
                .bodyToMono(Client.class);
    }

    public Mono<Client> save(Client client) {
        return webClient.post()
                .uri("/save")
                .bodyValue(client)
                .retrieve()
                .bodyToMono(Client.class);
    }

    public Mono<Void> saveAllInvalidTokens(Set<InvalidTokenEntity> invalidTokenEntities) {
        return webClient.post()
                .uri("/tokens/save-invalid")
                .bodyValue(invalidTokenEntities)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<InvalidTokenEntity> findInvalidTokenByTokenId(String tokenId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/tokens/find-invalid").queryParam("tokenId", tokenId).build())
                .retrieve()
                .bodyToMono(InvalidTokenEntity.class);
    }
}
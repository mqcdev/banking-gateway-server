package com.nttdata.banking.gateway.service.impl;

import com.nttdata.banking.gateway.client.UserServiceClient;
import com.nttdata.banking.gateway.exception.TokenAlreadyInvalidatedException;
import com.nttdata.banking.gateway.model.InvalidTokenEntity;
import com.nttdata.banking.gateway.service.InvalidTokenService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InvalidTokenServiceImpl implements InvalidTokenService {

    private final ObjectFactory<UserServiceClient> userServiceClientFactory;

    public InvalidTokenServiceImpl(ObjectFactory<UserServiceClient> userServiceClientFactory) {
        this.userServiceClientFactory = userServiceClientFactory;
    }

    @Override
    public Mono<Void> invalidateTokens(Set<String> tokenIds) {
        // Obtener el cliente bajo demanda
        UserServiceClient client = userServiceClientFactory.getObject();

        // Convertir los IDs de tokens a entidades InvalidTokenEntity
        Set<InvalidTokenEntity> invalidTokenEntities = tokenIds.stream()
                .map(tokenId -> InvalidTokenEntity.builder()
                        .tokenId(tokenId)
                        .createdAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toSet());

          client.saveAllInvalidTokens(invalidTokenEntities);
          return Mono.empty();
    }

    @Override
    public Mono<Void> checkForInvalidityOfToken(String tokenId) {
        // Obtener el cliente bajo demanda
        UserServiceClient client = userServiceClientFactory.getObject();
        return client.findInvalidTokenByTokenId(tokenId)
                .flatMap(entity -> Mono.error(new TokenAlreadyInvalidatedException(tokenId)))
                .then();
    }
}
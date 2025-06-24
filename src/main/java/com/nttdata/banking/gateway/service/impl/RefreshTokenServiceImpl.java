package com.nttdata.banking.gateway.service.impl;

import com.nttdata.banking.gateway.client.UserServiceClient;
import com.nttdata.banking.gateway.dto.request.TokenRefreshRequest;
import com.nttdata.banking.gateway.exception.UserNotFoundException;
import com.nttdata.banking.gateway.exception.UserStatusNotValidException;
import com.nttdata.banking.gateway.model.Client;
import com.nttdata.banking.gateway.model.Token;
import com.nttdata.banking.gateway.service.RefreshTokenService;
import com.nttdata.banking.gateway.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserServiceClient userServiceClient;
    private final TokenService tokenService;

    @Override
    public Mono<Token> refreshToken(final TokenRefreshRequest tokenRefreshRequest) {
        return tokenService.getPayload(tokenRefreshRequest.getRefreshToken())
                .map(claims -> claims.getSubject())
                .flatMap(email -> userServiceClient.findUserEntityByEmail(email)
                        .switchIfEmpty(Mono.error(new UserNotFoundException("No se encontró el usuario con email: " + email)))
                        .flatMap(this::validateUserStatus)
                        .flatMap(user -> tokenService.generateToken(user.getClaims(), tokenRefreshRequest.getRefreshToken()))
                );
    }

    private Mono<Client> validateUserStatus(final Client userEntity) {
        if (Boolean.FALSE.equals(userEntity.getState())) {
            return Mono.error(new UserStatusNotValidException("El usuario no está activo"));
        }
        return Mono.just(userEntity);
    }
}
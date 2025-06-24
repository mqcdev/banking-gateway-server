package com.nttdata.banking.gateway.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TokenValidationService {

    private final InvalidTokenService invalidTokenService;
    private final TokenService tokenService;

    public TokenValidationService(InvalidTokenService invalidTokenService, TokenService tokenService) {
        this.invalidTokenService = invalidTokenService;
        this.tokenService = tokenService;
    }

    public Mono<Boolean> checkTokenValidity(String jwt) {
        return tokenService.getId(jwt)
                .flatMap(tokenId -> invalidTokenService.checkForInvalidityOfToken(tokenId)
                        .thenReturn(true));
    }

    public Mono<Boolean> validateToken(String jwt) {
        return tokenService.verifyAndValidate(jwt)
                .then(tokenService.getId(jwt))
                .flatMap(tokenId -> invalidTokenService.checkForInvalidityOfToken(tokenId)
                        .thenReturn(true));
    }
}
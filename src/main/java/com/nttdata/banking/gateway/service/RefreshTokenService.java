package com.nttdata.banking.gateway.service;


import com.nttdata.banking.gateway.dto.request.TokenRefreshRequest;
import com.nttdata.banking.gateway.model.Token;
import reactor.core.publisher.Mono;

public interface RefreshTokenService {

    Mono<Token> refreshToken(final TokenRefreshRequest tokenRefreshRequest);

}

package com.nttdata.banking.gateway.service.impl;

import com.nttdata.banking.gateway.dto.request.TokenInvalidateRequest;
import com.nttdata.banking.gateway.service.InvalidTokenService;
import com.nttdata.banking.gateway.service.LogoutService;
import com.nttdata.banking.gateway.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final TokenService tokenService;
    private final InvalidTokenService invalidTokenService;

    @Override
    public Mono<Void> logout(TokenInvalidateRequest tokenInvalidateRequest) {
        return tokenService.verifyAndValidate(
                Set.of(
                        tokenInvalidateRequest.getAccessToken(),
                        tokenInvalidateRequest.getRefreshToken()
                )
        ).then(
                Mono.zip(
                        tokenService.getId(tokenInvalidateRequest.getAccessToken()),
                        tokenService.getId(tokenInvalidateRequest.getRefreshToken())
                ).flatMap(tuple -> {
                    String accessTokenId = tuple.getT1();
                    String refreshTokenId = tuple.getT2();
                    return Mono.when(
                            invalidTokenService.checkForInvalidityOfToken(accessTokenId),
                            invalidTokenService.checkForInvalidityOfToken(refreshTokenId)
                    ).then(
                            invalidTokenService.invalidateTokens(Set.of(accessTokenId, refreshTokenId))
                    );
                })
        );
    }
}
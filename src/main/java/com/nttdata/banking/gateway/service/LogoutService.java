package com.nttdata.banking.gateway.service;

import com.nttdata.banking.gateway.dto.request.TokenInvalidateRequest;
import reactor.core.publisher.Mono;

public interface LogoutService {

    Mono<Void> logout(final TokenInvalidateRequest tokenInvalidateRequest);

}
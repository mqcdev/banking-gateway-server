package com.nttdata.banking.gateway.service;



import com.nttdata.banking.gateway.dto.request.LoginRequest;
import com.nttdata.banking.gateway.model.Token;
import reactor.core.publisher.Mono;


public interface UserLoginService {

    Mono<Token> login(final LoginRequest loginRequest);

}

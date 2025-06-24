package com.nttdata.banking.gateway.service.impl;

import com.nttdata.banking.gateway.client.UserServiceClient;
import com.nttdata.banking.gateway.dto.request.LoginRequest;
import com.nttdata.banking.gateway.exception.UserNotFoundException;
import com.nttdata.banking.gateway.model.Token;
import com.nttdata.banking.gateway.service.TokenService;
import com.nttdata.banking.gateway.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserServiceClient userServiceClient;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Token> login(final LoginRequest loginRequest) {
        return userServiceClient.findUserEntityByEmail(loginRequest.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException("No se encontró el usuario con el email: " + loginRequest.getEmail())))
                .flatMap(client -> {
                    if (!passwordEncoder.matches(loginRequest.getPassword(), client.getPassword())) {
                        return Mono.error(new RuntimeException("Contraseña incorrecta"));
                    }
                    return tokenService.generateToken(client.getClaims());
                });
    }
}
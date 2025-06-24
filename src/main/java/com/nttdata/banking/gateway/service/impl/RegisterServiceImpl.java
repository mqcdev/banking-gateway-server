package com.nttdata.banking.gateway.service.impl;

import com.nttdata.banking.gateway.client.UserServiceClient;
import com.nttdata.banking.gateway.dto.request.RegisterRequest;
import com.nttdata.banking.gateway.exception.UserAlreadyExistException;
import com.nttdata.banking.gateway.model.Client;
import com.nttdata.banking.gateway.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Client> registerUser(final RegisterRequest registerRequest) {
        return Mono.fromCallable(() -> userServiceClient.existsUserEntityByEmail(registerRequest.getEmail()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new UserAlreadyExistException("El email ya está registrado"));
                    }
                    Client client = Client.builder()
                            .names(registerRequest.getNames())
                            .surnames(registerRequest.getSurnames())
                            .clientType(registerRequest.getClientType())
                            .documentType(registerRequest.getDocumentType())
                            .documentNumber(registerRequest.getDocumentNumber())
                            .cellphone(registerRequest.getCellphone())
                            .email(registerRequest.getEmail())
                            .userType(registerRequest.getUserType())
                            .password(passwordEncoder.encode(registerRequest.getPassword()))
                            .state(true)
                            .profile(registerRequest.getProfile())
                            .build();
                    return userServiceClient.save(client);
                });
    }
}
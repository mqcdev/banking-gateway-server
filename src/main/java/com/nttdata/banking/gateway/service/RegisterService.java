package com.nttdata.banking.gateway.service;


import com.nttdata.banking.gateway.dto.request.RegisterRequest;
import com.nttdata.banking.gateway.model.Client;
import reactor.core.publisher.Mono;

public interface RegisterService {

    Mono<Client> registerUser(final RegisterRequest registerRequest);

}
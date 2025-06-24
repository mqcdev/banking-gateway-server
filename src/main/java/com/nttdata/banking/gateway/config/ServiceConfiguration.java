package com.nttdata.banking.gateway.config;

import com.nttdata.banking.gateway.client.UserServiceClient;
import com.nttdata.banking.gateway.service.InvalidTokenService;
import com.nttdata.banking.gateway.service.TokenService;
import com.nttdata.banking.gateway.service.TokenValidationService;
import com.nttdata.banking.gateway.service.impl.InvalidTokenServiceImpl;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceConfiguration {

    @Bean
    @Primary
    public InvalidTokenService invalidTokenService(ObjectFactory<UserServiceClient> userServiceClientFactory) {
        return new InvalidTokenServiceImpl(userServiceClientFactory);
    }


}
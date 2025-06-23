package com.nttdata.banking.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userservice",  url = "http://ms-client:8080", path = "/api/auth")
public interface UserServiceClient {

    @PostMapping("/validate-token")
    void validateToken(@RequestParam String token);
}
package com.example.ordersconsumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoApiClientConfig {
    @Bean("goApiClient")
    public WebClient goApiClient(GoApiProperties goApiProperties) {
        return WebClient.builder()
                .baseUrl(goApiProperties.getBaseUrl())
                .build();
    }
}

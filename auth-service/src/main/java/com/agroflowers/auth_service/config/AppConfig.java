package com.agroflowers.auth_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Bean
    public WebClient keycloakWebClient() {
        return WebClient.builder()
                .baseUrl(keycloakServerUrl)
                .build();
    }
}

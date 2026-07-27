package com.agroflowers.logistics_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${catalog-service.base-url}")
    private String catalogServiceBaseUrl;

    @Bean
    public WebClient catalogServiceWebClient() {
        return WebClient.builder()
                .baseUrl(catalogServiceBaseUrl)
                .build();
    }
}

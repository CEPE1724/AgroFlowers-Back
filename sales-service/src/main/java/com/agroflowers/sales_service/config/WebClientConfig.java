package com.agroflowers.sales_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${logistics-service.base-url}")
    private String logisticsServiceBaseUrl;

    @Bean
    public WebClient logisticsServiceWebClient() {
        return WebClient.builder()
                .baseUrl(logisticsServiceBaseUrl)
                .build();
    }
}

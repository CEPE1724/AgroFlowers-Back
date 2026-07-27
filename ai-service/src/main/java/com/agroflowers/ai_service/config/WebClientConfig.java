package com.agroflowers.ai_service.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Value("${sales-service.base-url}")
    private String salesServiceBaseUrl;

    @Value("${claude.base-url}")
    private String claudeBaseUrl;

    @Bean
    public WebClient salesServiceWebClient() {
        return WebClient.builder()
                .baseUrl(salesServiceBaseUrl)
                .build();
    }

    @Bean
    public WebClient claudeWebClient(@Qualifier("claudeHttpClient") HttpClient httpClient) {
        return WebClient.builder()
                .baseUrl(claudeBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public HttpClient claudeHttpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30));
    }
}

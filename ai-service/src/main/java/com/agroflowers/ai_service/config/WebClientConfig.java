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

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Bean
    public WebClient salesServiceWebClient() {
        return WebClient.builder()
                .baseUrl(salesServiceBaseUrl)
                .build();
    }

    @Bean
    public WebClient ollamaWebClient(@Qualifier("ollamaHttpClient") HttpClient httpClient) {
        return WebClient.builder()
                .baseUrl(ollamaBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public HttpClient ollamaHttpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(90));
    }
}

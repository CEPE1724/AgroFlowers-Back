package com.agroflowers.ai_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.agroflowers.ai_service.config.ServiceUnavailableException;
import com.agroflowers.ai_service.dto.OllamaGenerateRequestDto;
import com.agroflowers.ai_service.dto.OllamaGenerateResponseDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class OllamaClient {

    private final WebClient ollamaWebClient;

    @Value("${ollama.model}")
    private String model;

    public OllamaClient(WebClient ollamaWebClient) {
        this.ollamaWebClient = ollamaWebClient;
    }

    @CircuitBreaker(name = "ollama", fallbackMethod = "generateFallback")
    @Retry(name = "ollama")
    public String generate(String systemPrompt, String userPrompt) {
        OllamaGenerateRequestDto request = new OllamaGenerateRequestDto(model, userPrompt, systemPrompt, false);

        OllamaGenerateResponseDto response = ollamaWebClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaGenerateResponseDto.class)
                .block();

        if (response == null || response.response() == null) {
            throw new ServiceUnavailableException("El asistente de IA no devolvio una respuesta");
        }

        return response.response().trim();
    }

    public String generateFallback(String systemPrompt, String userPrompt, Throwable throwable) {
        throw new ServiceUnavailableException("El asistente de IA no esta disponible en este momento");
    }
}

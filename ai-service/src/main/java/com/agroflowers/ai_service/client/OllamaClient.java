package com.agroflowers.ai_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.agroflowers.ai_service.config.ServiceUnavailableException;
import com.agroflowers.ai_service.dto.OllamaGenerateRequestDto;
import com.agroflowers.ai_service.dto.OllamaGenerateRequestDto.Options;
import com.agroflowers.ai_service.dto.OllamaGenerateResponseDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class OllamaClient {

    private static final Logger log = LoggerFactory.getLogger(OllamaClient.class);

    // Contexto y respuesta acotados a proposito: reduce el uso de RAM del modelo
    // local y evita respuestas largas innecesarias para un asistente de un parrafo.
    private static final int NUM_PREDICT = 250;
    private static final int NUM_CTX = 2048;

    private final WebClient ollamaWebClient;

    @Value("${ollama.model}")
    private String model;

    public OllamaClient(WebClient ollamaWebClient) {
        this.ollamaWebClient = ollamaWebClient;
    }

    @CircuitBreaker(name = "ollama", fallbackMethod = "generateFallback")
    @Retry(name = "ollama")
    public String generate(String systemPrompt, String userPrompt) {
        OllamaGenerateRequestDto request = new OllamaGenerateRequestDto(
                model, userPrompt, systemPrompt, false,
                new Options(NUM_PREDICT, NUM_CTX)
        );

        OllamaGenerateResponseDto response = ollamaWebClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaGenerateResponseDto.class)
                .block();

        if (response == null || response.response() == null || response.response().isBlank()) {
            throw new ServiceUnavailableException("El asistente de IA no devolvio una respuesta");
        }

        return response.response().trim();
    }

    public String generateFallback(String systemPrompt, String userPrompt, Throwable throwable) {
        if (throwable instanceof WebClientResponseException webEx) {
            log.error("Ollama respondio {} {}: {}", webEx.getStatusCode(), webEx.getStatusText(), webEx.getResponseBodyAsString());
        } else {
            log.error("Fallo la llamada a Ollama: {}", throwable.toString(), throwable);
        }
        throw new ServiceUnavailableException("El asistente de IA no esta disponible en este momento");
    }
}

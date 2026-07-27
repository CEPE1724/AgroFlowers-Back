package com.agroflowers.ai_service.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.agroflowers.ai_service.config.ServiceUnavailableException;
import com.agroflowers.ai_service.dto.ClaudeMessageRequestDto;
import com.agroflowers.ai_service.dto.ClaudeMessageRequestDto.Message;
import com.agroflowers.ai_service.dto.ClaudeMessageResponseDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(ClaudeClient.class);
    private static final int MAX_TOKENS = 1024;

    private final WebClient claudeWebClient;

    @Value("${claude.api-key}")
    private String apiKey;

    @Value("${claude.model}")
    private String model;

    public ClaudeClient(WebClient claudeWebClient) {
        this.claudeWebClient = claudeWebClient;
    }

    @CircuitBreaker(name = "claude", fallbackMethod = "generateFallback")
    @Retry(name = "claude")
    public String generate(String systemPrompt, String userPrompt) {
        ClaudeMessageRequestDto request = new ClaudeMessageRequestDto(
                model,
                MAX_TOKENS,
                systemPrompt,
                List.of(new Message("user", userPrompt))
        );

        ClaudeMessageResponseDto response = claudeWebClient.post()
                .uri("/v1/messages")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ClaudeMessageResponseDto.class)
                .block();

        String text = extractText(response);

        if (text == null || text.isBlank()) {
            throw new ServiceUnavailableException("El asistente de IA no devolvio una respuesta");
        }

        return text.trim();
    }

    private String extractText(ClaudeMessageResponseDto response) {
        if (response == null || response.content() == null || response.content().isEmpty()) {
            return null;
        }

        return response.content().stream()
                .filter(block -> "text".equals(block.type()))
                .map(ClaudeMessageResponseDto.ContentBlock::text)
                .findFirst()
                .orElse(null);
    }

    public String generateFallback(String systemPrompt, String userPrompt, Throwable throwable) {
        if (throwable instanceof WebClientResponseException webEx) {
            log.error("Claude respondio {} {}: {}", webEx.getStatusCode(), webEx.getStatusText(), webEx.getResponseBodyAsString());
        } else {
            log.error("Fallo la llamada a Claude: {}", throwable.toString(), throwable);
        }
        throw new ServiceUnavailableException("El asistente de IA no esta disponible en este momento");
    }
}

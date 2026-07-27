package com.agroflowers.ai_service.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.agroflowers.ai_service.config.ServiceUnavailableException;
import com.agroflowers.ai_service.dto.GeminiGenerateRequestDto;
import com.agroflowers.ai_service.dto.GeminiGenerateRequestDto.Content;
import com.agroflowers.ai_service.dto.GeminiGenerateRequestDto.GenerationConfig;
import com.agroflowers.ai_service.dto.GeminiGenerateRequestDto.Part;
import com.agroflowers.ai_service.dto.GeminiGenerateRequestDto.SystemInstruction;
import com.agroflowers.ai_service.dto.GeminiGenerateResponseDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final WebClient geminiWebClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public GeminiClient(WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
    }

    @CircuitBreaker(name = "gemini", fallbackMethod = "generateFallback")
    @Retry(name = "gemini")
    public String generate(String systemPrompt, String userPrompt) {
        GeminiGenerateRequestDto request = new GeminiGenerateRequestDto(
                new SystemInstruction(List.of(new Part(systemPrompt))),
                List.of(new Content(List.of(new Part(userPrompt)))),
                new GenerationConfig(0.4, 400)
        );

        GeminiGenerateResponseDto response = geminiWebClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiGenerateResponseDto.class)
                .block();

        String text = extractText(response);

        if (text == null || text.isBlank()) {
            throw new ServiceUnavailableException("El asistente de IA no devolvio una respuesta");
        }

        return text.trim();
    }

    private String extractText(GeminiGenerateResponseDto response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return null;
        }

        GeminiGenerateResponseDto.Content content = response.candidates().get(0).content();

        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return null;
        }

        return content.parts().get(0).text();
    }

    public String generateFallback(String systemPrompt, String userPrompt, Throwable throwable) {
        if (throwable instanceof WebClientResponseException webEx) {
            log.error("Gemini respondio {} {}: {}", webEx.getStatusCode(), webEx.getStatusText(), webEx.getResponseBodyAsString());
        } else {
            log.error("Fallo la llamada a Gemini: {}", throwable.toString(), throwable);
        }
        throw new ServiceUnavailableException("El asistente de IA no esta disponible en este momento");
    }
}

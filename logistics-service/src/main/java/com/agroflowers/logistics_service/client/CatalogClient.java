package com.agroflowers.logistics_service.client;

import java.util.NoSuchElementException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.agroflowers.logistics_service.dto.CatalogFarmDto;

@Component
public class CatalogClient {

    private final WebClient catalogServiceWebClient;

    public CatalogClient(WebClient catalogServiceWebClient) {
        this.catalogServiceWebClient = catalogServiceWebClient;
    }

    public String getFarmName(Long farmId, String bearerToken) {
        CatalogFarmDto farm = catalogServiceWebClient.get()
                .uri("/api/farms/{id}", farmId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(CatalogFarmDto.class)
                .block();

        if (farm == null) {
            throw new NoSuchElementException("No se encontro la finca con id: " + farmId);
        }

        return farm.name();
    }
}

package com.agroflowers.purchase_service.client;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.agroflowers.purchase_service.dto.CatalogFarmDto;
import com.agroflowers.purchase_service.dto.CatalogFlowerDto;

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

    public Map<Long, String> getFlowerDisplayNames(String bearerToken) {
        List<CatalogFlowerDto> flowers = catalogServiceWebClient.get()
                .uri("/api/flowers/all")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(CatalogFlowerDto.class)
                .collectList()
                .block();

        if (flowers == null) {
            return Map.of();
        }

        Function<CatalogFlowerDto, String> displayName = flower ->
                flower.flowerType() + " " + flower.variety() + " " + flower.stemLength() + " cm";

        return flowers.stream().collect(Collectors.toMap(CatalogFlowerDto::id, displayName));
    }
}

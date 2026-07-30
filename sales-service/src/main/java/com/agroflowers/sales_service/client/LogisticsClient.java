package com.agroflowers.sales_service.client;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.agroflowers.sales_service.config.ServiceUnavailableException;
import com.agroflowers.sales_service.dto.CostSummaryDto;
import com.agroflowers.sales_service.dto.ShipmentSummaryDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class LogisticsClient {

    private final WebClient logisticsServiceWebClient;

    public LogisticsClient(WebClient logisticsServiceWebClient) {
        this.logisticsServiceWebClient = logisticsServiceWebClient;
    }
     
    @CircuitBreaker(name = "logisticsService", fallbackMethod = "getShipmentFallback")
    @Retry(name = "logisticsService")
    public ShipmentSummaryDto getShipmentById(Long shipmentId, String bearerToken) {
        try {
            return logisticsServiceWebClient.get()
                    .uri("/api/shipments/{id}", shipmentId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .retrieve()
                    .bodyToMono(ShipmentSummaryDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new NoSuchElementException("No se encontro el embarque con id: " + shipmentId);
        }
    }

    public ShipmentSummaryDto getShipmentFallback(Long shipmentId, String bearerToken, Throwable throwable) {
        if (throwable instanceof NoSuchElementException noSuchElementException) {
            throw noSuchElementException;
        }
        throw new ServiceUnavailableException("El servicio de logistica no esta disponible en este momento");
    }

    @CircuitBreaker(name = "logisticsService", fallbackMethod = "getCostFallback")
    @Retry(name = "logisticsService")
    public Optional<CostSummaryDto> getCostByShipmentId(Long shipmentId, String bearerToken) {
        try {
            CostSummaryDto cost = logisticsServiceWebClient.get()
                    .uri("/api/costs/shipment/{shipmentId}", shipmentId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .retrieve()
                    .bodyToMono(CostSummaryDto.class)
                    .block();

            return Optional.ofNullable(cost);
        } catch (WebClientResponseException.NotFound ex) {
            return Optional.empty();
        }
    }

    public Optional<CostSummaryDto> getCostFallback(Long shipmentId, String bearerToken, Throwable throwable) {
        return Optional.empty();
    }

    @CircuitBreaker(name = "logisticsService", fallbackMethod = "getAllShipmentsFallback")
    @Retry(name = "logisticsService")
    public List<ShipmentSummaryDto> getAllShipments(String bearerToken) {
        return logisticsServiceWebClient.get()
                .uri("/api/shipments/all")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(ShipmentSummaryDto.class)
                .collectList()
                .block();
    }

    public List<ShipmentSummaryDto> getAllShipmentsFallback(String bearerToken, Throwable throwable) {
        return List.of();
    }

    @CircuitBreaker(name = "logisticsService", fallbackMethod = "getAllCostsFallback")
    @Retry(name = "logisticsService")
    public List<CostSummaryDto> getAllCosts(String bearerToken) {
        return logisticsServiceWebClient.get()
                .uri("/api/costs/all")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(CostSummaryDto.class)
                .collectList()
                .block();
    }

    public List<CostSummaryDto> getAllCostsFallback(String bearerToken, Throwable throwable) {
        return List.of();
    }
}

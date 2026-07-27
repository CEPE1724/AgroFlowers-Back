package com.agroflowers.ai_service.client;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.agroflowers.ai_service.dto.DashboardChartsDto;
import com.agroflowers.ai_service.dto.DashboardSummaryDto;
import com.agroflowers.ai_service.dto.ProfitabilityResponseDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class SalesDataClient {

    private final WebClient salesServiceWebClient;

    public SalesDataClient(WebClient salesServiceWebClient) {
        this.salesServiceWebClient = salesServiceWebClient;
    }

    @CircuitBreaker(name = "salesService", fallbackMethod = "getSummaryFallback")
    @Retry(name = "salesService")
    public DashboardSummaryDto getSummary(String bearerToken) {
        return salesServiceWebClient.get()
                .uri("/api/dashboard/summary")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(DashboardSummaryDto.class)
                .block();
    }

    public DashboardSummaryDto getSummaryFallback(String bearerToken, Throwable throwable) {
        return null;
    }

    @CircuitBreaker(name = "salesService", fallbackMethod = "getChartsFallback")
    @Retry(name = "salesService")
    public DashboardChartsDto getCharts(String bearerToken) {
        return salesServiceWebClient.get()
                .uri("/api/dashboard/charts")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(DashboardChartsDto.class)
                .block();
    }

    public DashboardChartsDto getChartsFallback(String bearerToken, Throwable throwable) {
        return null;
    }

    @CircuitBreaker(name = "salesService", fallbackMethod = "getProfitabilityFallback")
    @Retry(name = "salesService")
    public List<ProfitabilityResponseDto> getProfitability(String bearerToken) {
        return salesServiceWebClient.get()
                .uri("/api/profitability")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(ProfitabilityResponseDto.class)
                .collectList()
                .block();
    }

    public List<ProfitabilityResponseDto> getProfitabilityFallback(String bearerToken, Throwable throwable) {
        return List.of();
    }
}

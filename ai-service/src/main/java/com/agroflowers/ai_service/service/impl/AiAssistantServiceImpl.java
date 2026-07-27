package com.agroflowers.ai_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agroflowers.ai_service.client.OllamaClient;
import com.agroflowers.ai_service.client.SalesDataClient;
import com.agroflowers.ai_service.dto.AiAssistantResponseDto;
import com.agroflowers.ai_service.dto.DashboardChartsDto;
import com.agroflowers.ai_service.dto.DashboardSummaryDto;
import com.agroflowers.ai_service.dto.ProfitabilityResponseDto;
import com.agroflowers.ai_service.service.AiAssistantService;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    private static final String SYSTEM_PROMPT = """
            Eres el asistente de analisis de negocio de AgroFlowers AI, una empresa exportadora de flores.
            Respondes siempre en espanol, de forma breve, clara y concreta (maximo un parrafo corto).
            Debes basar tus respuestas UNICAMENTE en los datos reales que se te proporcionan a continuacion.
            Si la pregunta no se puede responder con esos datos, dilo claramente en lugar de inventar cifras.
            No uses formato markdown, responde en texto plano.
            """;

    private static final List<String> WARNING_KEYWORDS = List.of(
            "riesgo", "atencion", "alerta", "perdida", "bajo margen", "revisar", "preocup", "negativ"
    );

    private final SalesDataClient salesDataClient;
    private final OllamaClient ollamaClient;

    public AiAssistantServiceImpl(SalesDataClient salesDataClient, OllamaClient ollamaClient) {
        this.salesDataClient = salesDataClient;
        this.ollamaClient = ollamaClient;
    }

    @Override
    public AiAssistantResponseDto answer(String question, String bearerToken) {
        DashboardSummaryDto summary = salesDataClient.getSummary(bearerToken);
        DashboardChartsDto charts = salesDataClient.getCharts(bearerToken);
        List<ProfitabilityResponseDto> profitability = salesDataClient.getProfitability(bearerToken);

        String context = buildContext(summary, charts, profitability);
        String userPrompt = context + "\nPregunta del usuario: " + question;

        String content = ollamaClient.generate(SYSTEM_PROMPT, userPrompt);

        return new AiAssistantResponseDto(content, detectWarning(content));
    }

    private String buildContext(DashboardSummaryDto summary, DashboardChartsDto charts, List<ProfitabilityResponseDto> profitability) {
        StringBuilder sb = new StringBuilder();

        if (summary != null) {
            sb.append("Resumen general del negocio (mes actual):\n");
            sb.append("- Embarques este mes: ").append(summary.shipmentsThisMonth()).append("\n");
            sb.append("- Ventas este mes: $").append(round(summary.salesThisMonth())).append("\n");
            sb.append("- Costo total este mes: $").append(round(summary.totalCostThisMonth())).append("\n");
            sb.append("- Utilidad total: $").append(round(summary.totalProfit())).append("\n");
            sb.append("- Margen promedio: ").append(round(summary.averageMargin())).append("%\n");
            sb.append("- Finca mas rentable: ").append(nullSafe(summary.mostProfitableFarm())).append("\n");
            sb.append("- Variedad mas vendida: ").append(nullSafe(summary.bestSellingVariety())).append("\n");
            sb.append("- Embarques con margen bajo: ").append(summary.lowMarginShipments()).append("\n\n");
        }

        if (charts != null) {
            if (notEmpty(charts.farmProfitability())) {
                sb.append("Rentabilidad por finca:\n");
                charts.farmProfitability().forEach(p ->
                        sb.append("- ").append(p.farmName()).append(": ").append(round(p.profitMargin())).append("%\n"));
                sb.append("\n");
            }

            if (notEmpty(charts.costsByCategory())) {
                sb.append("Costos por categoria:\n");
                charts.costsByCategory().forEach(c ->
                        sb.append("- ").append(c.category()).append(": $").append(round(c.amount())).append("\n"));
                sb.append("\n");
            }

            if (notEmpty(charts.salesByVariety())) {
                sb.append("Ventas por variedad:\n");
                charts.salesByVariety().forEach(s ->
                        sb.append("- ").append(s.variety()).append(": $").append(round(s.totalSold())).append("\n"));
                sb.append("\n");
            }
        }

        if (notEmpty(profitability)) {
            sb.append("Detalle de embarques (rentabilidad individual):\n");
            profitability.stream()
                    .limit(40)
                    .forEach(p -> sb.append("- ").append(p.shipmentNumber())
                            .append(" (").append(p.shipmentDate()).append("), finca=").append(nullSafe(p.farmName()))
                            .append(", cliente=").append(nullSafe(p.customer()))
                            .append(", venta=$").append(round(p.totalSale()))
                            .append(", costo=$").append(round(p.totalCost()))
                            .append(", utilidad=$").append(round(p.profit()))
                            .append(", margen=").append(round(p.profitMargin())).append("%")
                            .append(" (").append(p.classification()).append(")\n"));
        }

        if (sb.isEmpty()) {
            sb.append("No hay datos disponibles del sistema en este momento.\n");
        }

        return sb.toString();
    }

    private boolean detectWarning(String content) {
        String lower = content.toLowerCase();
        return WARNING_KEYWORDS.stream().anyMatch(lower::contains);
    }

    private boolean notEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    private String round(Double value) {
        return value == null ? "N/D" : String.format("%.2f", value);
    }

    private String round(double value) {
        return String.format("%.2f", value);
    }

    private String nullSafe(String value) {
        return (value == null || value.isBlank()) ? "N/D" : value;
    }
}

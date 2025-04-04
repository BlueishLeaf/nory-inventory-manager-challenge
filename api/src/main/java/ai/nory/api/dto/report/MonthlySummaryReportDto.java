package ai.nory.api.dto.report;

import java.math.BigDecimal;

public record MonthlySummaryReportDto (
        BigDecimal deliveryCost,
        BigDecimal revenue,
        BigDecimal inventoryValue,
        BigDecimal wasteCost
) { }

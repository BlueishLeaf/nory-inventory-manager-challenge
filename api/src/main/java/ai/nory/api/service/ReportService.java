package ai.nory.api.service;

import ai.nory.api.dto.delivery.DeliveryDto;
import ai.nory.api.dto.ingredient.LocationIngredientDto;
import ai.nory.api.dto.report.GenerateReportCommand;
import ai.nory.api.dto.report.InventoryAuditLogDto;
import ai.nory.api.dto.report.MonthlySummaryReportDto;
import ai.nory.api.dto.sale.SaleDto;
import ai.nory.api.dto.sale.SaleItemDto;
import ai.nory.api.dto.stocktake.StocktakeDto;
import ai.nory.api.enumerator.QuantityChangeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final SaleService saleService;
    private final DeliveryService deliveryService;
    private final StocktakeService stocktakeService;
    private final IngredientService ingredientService;

    public MonthlySummaryReportDto generateMonthlySummaryReportForPeriod(GenerateReportCommand generateReportCommand) {
        // Total cost of deliveries
        BigDecimal monthlyDeliveriesCost = calculateMonthlyDeliveriesCost(generateReportCommand);

        // Total revenue from sales
        BigDecimal monthlySalesRevenue = calculateMonthlySalesRevenue(generateReportCommand);

        // Total value of current inventory
        BigDecimal currentInventoryValue = calculateCurrentInventoryValue(generateReportCommand);

        // Total cost of waste
        BigDecimal monthlyWasteCost = calculateTotalWasteCost(generateReportCommand);

        return new MonthlySummaryReportDto(
                monthlyDeliveriesCost,
                monthlySalesRevenue,
                currentInventoryValue,
                monthlyWasteCost
        );
    }

    private BigDecimal calculateTotalWasteCost(GenerateReportCommand generateReportCommand) {
        List<StocktakeDto> monthlyStockTakes = stocktakeService.findStocktakesForPeriod(
                generateReportCommand.locationDto().getId(),
                generateReportCommand.reportCriteriaDto().fromDate(),
                generateReportCommand.reportCriteriaDto().toDate()
        );
        return monthlyStockTakes.stream()
                .map(StocktakeDto::getInventoryAuditLogs)
                .flatMap(Collection::stream)
                .filter(inventoryAuditLog -> inventoryAuditLog.getQuantityChangeType().equals(QuantityChangeType.WASTE))
                .map(InventoryAuditLogDto::getQuantityChangeCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateCurrentInventoryValue(GenerateReportCommand generateReportCommand) {
        List<LocationIngredientDto> locationIngredients = ingredientService.getIngredients(generateReportCommand.locationDto().getId());
        return locationIngredients.stream()
                .map(locationIngredient ->
                        locationIngredient.getQuantity().multiply(locationIngredient.getIngredient().getCost())
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMonthlyDeliveriesCost(GenerateReportCommand generateReportCommand) {
        List<DeliveryDto> monthlyDeliveries = deliveryService.getDeliveriesForPeriod(
                generateReportCommand.locationDto().getId(),
                generateReportCommand.reportCriteriaDto().fromDate(),
                generateReportCommand.reportCriteriaDto().toDate()
        );
        return monthlyDeliveries.stream()
                .map(DeliveryDto::getInventoryAuditLogs)
                .flatMap(Collection::stream)
                .map(InventoryAuditLogDto::getQuantityChangeCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMonthlySalesRevenue(GenerateReportCommand generateReportCommand) {
        List<SaleDto> monthlySales = saleService.getSalesForPeriod(
                generateReportCommand.locationDto().getId(),
                generateReportCommand.reportCriteriaDto().fromDate(),
                generateReportCommand.reportCriteriaDto().toDate()
        );
        return monthlySales.stream()
                .map(SaleDto::getSaleItems)
                .flatMap(Collection::stream)
                .map(SaleItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

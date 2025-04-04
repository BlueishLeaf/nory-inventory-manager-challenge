package ai.nory.api.dto.stocktake;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StockCorrectionDto (@NotNull Long ingredientId, @NotNull BigDecimal quantityCounted) { }

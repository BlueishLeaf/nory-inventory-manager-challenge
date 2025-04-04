package ai.nory.api.dto.stocktake;

import jakarta.validation.Valid;

import java.util.List;

public record CreateStocktakeDto (@Valid List<StockCorrectionDto> stockCorrections) { }

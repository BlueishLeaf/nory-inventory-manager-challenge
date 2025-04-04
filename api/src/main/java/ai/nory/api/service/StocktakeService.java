package ai.nory.api.service;

import ai.nory.api.dto.ingredient.ChangeQuantityCommand;
import ai.nory.api.dto.ingredient.LocationIngredientDto;
import ai.nory.api.dto.stocktake.CreateStocktakeCommand;
import ai.nory.api.dto.stocktake.StockCorrectionDto;
import ai.nory.api.dto.stocktake.StocktakeDto;
import ai.nory.api.entity.InventoryAuditLog;
import ai.nory.api.entity.Stocktake;
import ai.nory.api.enumerator.QuantityChangeType;
import ai.nory.api.mapper.StocktakeDtoMapper;
import ai.nory.api.repository.StocktakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StocktakeService {
    private final StocktakeRepository stocktakeRepository;

    private final IngredientService ingredientService;

    public List<StocktakeDto> findStocktakesForPeriod(Long locationId, Instant fromDate, Instant toDate) {
        List<Stocktake> stocktakesForPeriod = stocktakeRepository.findByLocationIdAndCreatedAtBetween(locationId, fromDate, toDate);
        return StocktakeDtoMapper.INSTANCE.fromEntity(stocktakesForPeriod);
    }

    public StocktakeDto createStocktake(CreateStocktakeCommand createStocktakeCommand) {
        // Construct the audit logs for this stocktake
        Set<InventoryAuditLog> inventoryAuditLogs = constructStocktakeData(createStocktakeCommand);

        // Persist the Stocktake entity in the DB
        Stocktake stocktake = Stocktake.builder()
                .locationId(createStocktakeCommand.locationDto().getId())
                .staffMemberId(createStocktakeCommand.staffMemberDto().getId())
                .inventoryAuditLogs(inventoryAuditLogs)
                .createdBy(createStocktakeCommand.staffMemberDto().getName())
                .updatedBy(createStocktakeCommand.staffMemberDto().getName())
                .build();
        stocktake = stocktakeRepository.save(stocktake);
        StocktakeDto stocktakeDto = StocktakeDtoMapper.INSTANCE.fromEntity(stocktake);

        // Apply the stock changes from the stocktake to the location's inventory
        ChangeQuantityCommand changeQuantityCommand = new ChangeQuantityCommand(createStocktakeCommand.locationDto(), stocktakeDto.getInventoryAuditLogs());
        ingredientService.applyQuantityChange(changeQuantityCommand);

        return stocktakeDto;
    }

    private Set<InventoryAuditLog> constructStocktakeData(CreateStocktakeCommand createStocktakeCommand) {
        List<LocationIngredientDto> locationIngredients = ingredientService.getIngredients(createStocktakeCommand.locationDto().getId());

        Set<InventoryAuditLog> inventoryAuditLogs = new HashSet<>();
        createStocktakeCommand.createStocktakeDto().stockCorrections().forEach(stockCorrectionDto -> {
            LocationIngredientDto locationIngredient = validateLocationIngredientExists(stockCorrectionDto, locationIngredients);

            BigDecimal quantityDifference = locationIngredient.getQuantity().subtract(stockCorrectionDto.quantityCounted());
            boolean isWaste = quantityDifference.compareTo(BigDecimal.ZERO) > 0;
            BigDecimal absQuantityDifference = quantityDifference.abs();
            BigDecimal changeCost = locationIngredient.getIngredient().getCost().multiply(absQuantityDifference);

            // Construct a changelog for this action
            inventoryAuditLogs.add(InventoryAuditLog.builder()
                    .locationId(createStocktakeCommand.locationDto().getId())
                    .staffMemberId(createStocktakeCommand.staffMemberDto().getId())
                    .staffMemberName(createStocktakeCommand.staffMemberDto().getName())
                    .ingredientId(stockCorrectionDto.ingredientId())
                    .ingredientName(locationIngredient.getIngredient().getName())
                    .quantityChangeType(isWaste ? QuantityChangeType.WASTE : QuantityChangeType.CORRECTION)
                    .quantityBefore(locationIngredient.getQuantity())
                    .quantityAfter(isWaste ?
                            locationIngredient.getQuantity().subtract(absQuantityDifference) :
                            locationIngredient.getQuantity().add(absQuantityDifference)
                    )
                    .quantityChangeAmount(absQuantityDifference)
                    .quantityChangeCost(changeCost)
                    .createdBy(createStocktakeCommand.staffMemberDto().getName())
                    .updatedBy(createStocktakeCommand.staffMemberDto().getName())
                    .build());
        });
        return inventoryAuditLogs;
    }

    private static LocationIngredientDto validateLocationIngredientExists(StockCorrectionDto stockCorrectionDto, List<LocationIngredientDto> locationIngredients) {
        Optional<LocationIngredientDto> locationIngredientOptional = locationIngredients.stream()
                .filter(locationIngredient -> locationIngredient.getIngredientId().equals(stockCorrectionDto.ingredientId()))
                .findFirst();
        if (locationIngredientOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found for id: " + stockCorrectionDto.ingredientId());
        }
        return locationIngredientOptional.get();
    }
}

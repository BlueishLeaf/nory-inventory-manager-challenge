package ai.nory.api.service;

import ai.nory.api.dto.ingredient.ChangeQuantityCommand;
import ai.nory.api.dto.ingredient.LocationIngredientDto;
import ai.nory.api.dto.sale.CreateSaleCommand;
import ai.nory.api.dto.sale.SaleData;
import ai.nory.api.dto.sale.SaleDto;
import ai.nory.api.dto.sale.SoldItemDto;
import ai.nory.api.entity.InventoryAuditLog;
import ai.nory.api.entity.MenuItem;
import ai.nory.api.entity.Modifier;
import ai.nory.api.entity.Sale;
import ai.nory.api.entity.SaleItem;
import ai.nory.api.enumerator.ModifierCategory;
import ai.nory.api.enumerator.QuantityChangeType;
import ai.nory.api.mapper.SaleDtoMapper;
import ai.nory.api.repository.MenuItemRepository;
import ai.nory.api.repository.ModifierRepository;
import ai.nory.api.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository saleRepository;
    private final MenuItemRepository menuItemRepository;
    private final ModifierRepository modifierRepository;

    private final IngredientService ingredientService;

    public List<SaleDto> getSalesForPeriod(Long locationId, Instant fromDate, Instant toDate) {
        List<Sale> salesForPeriod = saleRepository.findByLocationIdAndCreatedAtBetween(locationId, fromDate, toDate);
        return SaleDtoMapper.INSTANCE.fromEntity(salesForPeriod);
    }

    public SaleDto createSale(CreateSaleCommand createSaleCommand) {
        // Construct the sale items and audit logs for this sale
        SaleData saleData = constructSaleData(createSaleCommand);

        Sale sale = Sale.builder()
                .locationId(createSaleCommand.locationDto().getId())
                .staffMemberId(createSaleCommand.staffMemberDto().getId())
                .saleItems(saleData.saleItems())
                .inventoryAuditLogs(saleData.inventoryAuditLogs())
                .createdBy(createSaleCommand.staffMemberDto().getName())
                .updatedBy(createSaleCommand.staffMemberDto().getName())
                .build();
        sale = saleRepository.save(sale);

        SaleDto saleDto = SaleDtoMapper.INSTANCE.fromEntity(sale);

        // Apply the stock changes from the sale to the location's inventory
        ChangeQuantityCommand changeQuantityCommand = new ChangeQuantityCommand(createSaleCommand.locationDto(), saleDto.getInventoryAuditLogs());
        ingredientService.applyQuantityChange(changeQuantityCommand);

        return saleDto;
    }

    private SaleData constructSaleData(CreateSaleCommand createSaleCommand) {
        // Fetch these straight from the repository as we need references to them later
        List<MenuItem> locationMenuItems = menuItemRepository.findByKeyLocationId(createSaleCommand.locationDto().getId());
        List<Modifier> modifiers = modifierRepository.findAll();

        List<SaleItem> saleItems = new ArrayList<>();
        Map<Long, InventoryAuditLog> auditLogIngredientMap = new HashMap<>();
        createSaleCommand.createSaleDto().soldItems().forEach(soldItem -> {
            // Ensure menu item exists in this location
            MenuItem menuItem = validateMenuItemExists(locationMenuItems, soldItem);

            // Ensure modifiers are valid for this menu item if they exist
            Set<Modifier> saleItemModifiers = validateModifiersForSoldItem(soldItem.modifierIds(), modifiers, menuItem);

            // Tally up the total price of the item including modifiers and quantity
            BigDecimal modifierPrice = saleItemModifiers.stream().map(Modifier::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal menuItemPriceWitModifiers = menuItem.getPrice().add(modifierPrice);
            BigDecimal soldItemQuantity = BigDecimal.valueOf(soldItem.quantity());
            BigDecimal totalPrice = menuItemPriceWitModifiers.multiply(soldItemQuantity);

            saleItems.add(SaleItem.builder()
                    .menuItem(menuItem)
                    .modifiers(saleItemModifiers)
                    .quantity(soldItem.quantity())
                    .totalPrice(totalPrice)
                    .createdBy(createSaleCommand.staffMemberDto().getName())
                    .updatedBy(createSaleCommand.staffMemberDto().getName())
                    .build());

            updateAuditLogMap(createSaleCommand, menuItem, soldItemQuantity, auditLogIngredientMap, saleItemModifiers);
        });

        return new SaleData(saleItems, new HashSet<>(auditLogIngredientMap.values()));
    }

    private void updateAuditLogMap(CreateSaleCommand createSaleCommand, MenuItem menuItem, BigDecimal soldItemQuantity, Map<Long, InventoryAuditLog> auditLogIngredientMap, Set<Modifier> saleItemModifiers) {
        List<LocationIngredientDto> locationIngredients = ingredientService.getIngredients(createSaleCommand.locationDto().getId());

        // Only want to create one audit log for each ingredient per sale
        menuItem.getRecipe().getIngredients().forEach(recipeIngredient -> {
            LocationIngredientDto locationIngredient = validateLocationIngredientExistsById(recipeIngredient.getKey().getIngredientId(), locationIngredients);

            BigDecimal additionalChangeAmount = recipeIngredient.getQuantity().multiply(soldItemQuantity);
            BigDecimal additionalChangeCost = recipeIngredient.getIngredient().getCost()
                    .multiply(recipeIngredient.getQuantity())
                    .multiply(soldItemQuantity);
            addOrUpdateAuditLogEntry(createSaleCommand, auditLogIngredientMap, additionalChangeAmount, additionalChangeCost, locationIngredient);
        });
        applyModifiers(createSaleCommand, auditLogIngredientMap, saleItemModifiers, locationIngredients, soldItemQuantity);
    }

    private static LocationIngredientDto validateLocationIngredientExistsById(Long ingredientId, List<LocationIngredientDto> locationIngredients) {
        Optional<LocationIngredientDto> locationIngredientOptional = locationIngredients.stream()
                .filter(locationIngredient -> locationIngredient.getIngredientId().equals(ingredientId))
                .findFirst();
        if (locationIngredientOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found for id: " + ingredientId);
        }
        return locationIngredientOptional.get();
    }

    private void applyModifiers(CreateSaleCommand createSaleCommand, Map<Long, InventoryAuditLog> auditLogIngredientMap, Set<Modifier> saleItemModifiers, List<LocationIngredientDto> locationIngredients, BigDecimal soldItemQuantity) {
        // Apply any extra added ingredients from modifiers
        saleItemModifiers.stream()
                .filter(modifier -> modifier.getModifierType().getName().equals(ModifierCategory.ADDED_INGREDIENT))
                .forEach(modifier -> {
                    LocationIngredientDto locationIngredient = validateLocationIngredientExistsByName(modifier.getOption(), locationIngredients);
                    BigDecimal additionalCost = soldItemQuantity.multiply(locationIngredient.getIngredient().getCost());
                    addOrUpdateAuditLogEntry(createSaleCommand, auditLogIngredientMap, soldItemQuantity, additionalCost, locationIngredient);
                });
    }

    private static LocationIngredientDto validateLocationIngredientExistsByName(String ingredientName, List<LocationIngredientDto> locationIngredients) {
        Optional<LocationIngredientDto> modifierIngredientOptional = locationIngredients.stream()
                .filter(locationIngredient -> locationIngredient.getIngredient().getName().equalsIgnoreCase(ingredientName))
                .findFirst();
        if (modifierIngredientOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No ingredient found for modifier with name: " + ingredientName);
        }
        return modifierIngredientOptional.get();
    }

    private void addOrUpdateAuditLogEntry(CreateSaleCommand createSaleCommand, Map<Long, InventoryAuditLog> auditLogIngredientMap, BigDecimal additionalChangeAmount, BigDecimal additionalChangeCost, LocationIngredientDto locationIngredient) {
        if (auditLogIngredientMap.containsKey(locationIngredient.getIngredientId())) {
            InventoryAuditLog inventoryAuditLog = auditLogIngredientMap.get(locationIngredient.getIngredientId());
            inventoryAuditLog.setQuantityAfter(inventoryAuditLog.getQuantityAfter().subtract(additionalChangeAmount));
            inventoryAuditLog.setQuantityChangeAmount(inventoryAuditLog.getQuantityChangeAmount().add(additionalChangeAmount));
            inventoryAuditLog.setQuantityChangeCost(inventoryAuditLog.getQuantityChangeCost().add(additionalChangeCost));
        } else {
            InventoryAuditLog inventoryAuditLog = InventoryAuditLog.builder()
                    .locationId(createSaleCommand.locationDto().getId())
                    .staffMemberId(createSaleCommand.staffMemberDto().getId())
                    .staffMemberName(createSaleCommand.staffMemberDto().getName())
                    .ingredientId(locationIngredient.getIngredientId())
                    .ingredientName(locationIngredient.getIngredient().getName())
                    .quantityChangeType(QuantityChangeType.SALE)
                    .quantityBefore(locationIngredient.getQuantity())
                    .quantityAfter(locationIngredient.getQuantity().subtract(additionalChangeAmount))
                    .quantityChangeAmount(additionalChangeAmount)
                    .quantityChangeCost(additionalChangeCost)
                    .createdBy(createSaleCommand.staffMemberDto().getName())
                    .updatedBy(createSaleCommand.staffMemberDto().getName())
                    .build();
            auditLogIngredientMap.put(locationIngredient.getIngredientId(), inventoryAuditLog);
        }
    }

    private MenuItem validateMenuItemExists(List<MenuItem> locationMenuItems, SoldItemDto soldItemDto) {
        Optional<MenuItem> menuItemOptional = locationMenuItems.stream()
                .filter(menuItem -> menuItem.getKey().getRecipeId().equals(soldItemDto.recipeId()))
                .findFirst();
        if (menuItemOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found for id: " + soldItemDto.recipeId());
        }
        return menuItemOptional.get();
    }

    private Set<Modifier> validateModifiersForSoldItem(Set<Long> modifierIds, List<Modifier> modifiers, MenuItem menuItem) {
        if (modifierIds == null || modifierIds.isEmpty()) {
            return new HashSet<>();
        }

        return modifierIds.stream().map(id -> {
            // Ensure requested modifier exists
            Optional<Modifier> modifierOptional = modifiers.stream()
                    .filter(modifier -> id.equals(modifier.getId()))
                    .findFirst();
            if (modifierOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Modifier not found for id: " + id);
            }
            Modifier modifier = modifierOptional.get();

            if (!modifier.getModifierType().equals(menuItem.getModifierType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Modifier type id mismatch");
            }
            return modifier;
        }).collect(Collectors.toSet());
    }
}

package ai.nory.api.service;

import ai.nory.api.dto.delivery.CreateDeliveryCommand;
import ai.nory.api.dto.delivery.DeliveredItemDto;
import ai.nory.api.dto.delivery.DeliveryDto;
import ai.nory.api.dto.ingredient.ChangeQuantityCommand;
import ai.nory.api.dto.ingredient.LocationIngredientDto;
import ai.nory.api.entity.Delivery;
import ai.nory.api.entity.InventoryAuditLog;
import ai.nory.api.enumerator.QuantityChangeType;
import ai.nory.api.mapper.DeliveryDtoMapper;
import ai.nory.api.repository.DeliveryRepository;
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
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    private final IngredientService ingredientService;

    public List<DeliveryDto> getDeliveriesForPeriod(Long locationId, Instant fromDate, Instant toDate) {
        List<Delivery> deliveriesForPeriod = deliveryRepository.findByLocationIdAndCreatedAtBetween(locationId, fromDate, toDate);
        return DeliveryDtoMapper.INSTANCE.fromEntity(deliveriesForPeriod);
    }

    public DeliveryDto createDelivery(CreateDeliveryCommand createDeliveryCommand) {
        // Construct the audit logs for this delivery
        Set<InventoryAuditLog> inventoryAuditLogs = constructDeliveryData(createDeliveryCommand);

        // Persist the Delivery entity in the DB
        Delivery delivery = Delivery.builder()
                .locationId(createDeliveryCommand.locationDto().getId())
                .staffMemberId(createDeliveryCommand.staffMemberDto().getId())
                .inventoryAuditLogs(inventoryAuditLogs)
                .createdBy(createDeliveryCommand.staffMemberDto().getName())
                .updatedBy(createDeliveryCommand.staffMemberDto().getName())
                .build();
        delivery = deliveryRepository.save(delivery);
        DeliveryDto deliveryDto = DeliveryDtoMapper.INSTANCE.fromEntity(delivery);

        // Apply the stock changes from the delivery to the location's inventory
        ChangeQuantityCommand changeQuantityCommand = new ChangeQuantityCommand(createDeliveryCommand.locationDto(), deliveryDto.getInventoryAuditLogs());
        ingredientService.applyQuantityChange(changeQuantityCommand);

        return deliveryDto;
    }

    private Set<InventoryAuditLog> constructDeliveryData(CreateDeliveryCommand createDeliveryCommand) {
        List<LocationIngredientDto> locationIngredients = ingredientService.getIngredients(createDeliveryCommand.locationDto().getId());

        Set<InventoryAuditLog> inventoryAuditLogs = new HashSet<>();
        createDeliveryCommand.createDeliveryDto().deliveredItems().forEach(deliveredItemDto -> {
            LocationIngredientDto locationIngredient = validateLocationIngredientExists(deliveredItemDto, locationIngredients);

            // Construct a changelog for this action
            BigDecimal quantityChangeCost = locationIngredient.getIngredient().getCost().multiply(deliveredItemDto.quantityDelivered());
            inventoryAuditLogs.add(InventoryAuditLog.builder()
                    .locationId(createDeliveryCommand.locationDto().getId())
                    .staffMemberId(createDeliveryCommand.staffMemberDto().getId())
                    .staffMemberName(createDeliveryCommand.staffMemberDto().getName())
                    .ingredientId(deliveredItemDto.ingredientId())
                    .ingredientName(locationIngredient.getIngredient().getName())
                    .quantityChangeType(QuantityChangeType.DELIVERY)
                    .quantityBefore(locationIngredient.getQuantity())
                    .quantityAfter(locationIngredient.getQuantity().add(deliveredItemDto.quantityDelivered()))
                    .quantityChangeAmount(deliveredItemDto.quantityDelivered())
                    .quantityChangeCost(quantityChangeCost)
                    .createdBy(createDeliveryCommand.staffMemberDto().getName())
                    .updatedBy(createDeliveryCommand.staffMemberDto().getName())
                    .build());
        });
        return inventoryAuditLogs;
    }

    private static LocationIngredientDto validateLocationIngredientExists(DeliveredItemDto deliveredItemDto, List<LocationIngredientDto> locationIngredients) {
        Optional<LocationIngredientDto> locationIngredientOptional = locationIngredients.stream()
                .filter(locationIngredient -> locationIngredient.getIngredientId().equals(deliveredItemDto.ingredientId()))
                .findFirst();
        if (locationIngredientOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found for id: " + deliveredItemDto.ingredientId());
        }
        return locationIngredientOptional.get();
    }
}

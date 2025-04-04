package ai.nory.api.service;

import ai.nory.api.dto.InventoryAuditLogDto;
import ai.nory.api.dto.ingredient.ChangeQuantityCommand;
import ai.nory.api.dto.ingredient.LocationIngredientDto;
import ai.nory.api.entity.LocationIngredient;
import ai.nory.api.entity.embedded.LocationIngredientKey;
import ai.nory.api.mapper.LocationIngredientDtoMapper;
import ai.nory.api.repository.LocationIngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final LocationIngredientRepository locationIngredientRepository;

    public List<LocationIngredientDto> getIngredients(Long locationId) {
        List<LocationIngredient> locationIngredients = locationIngredientRepository.findByKeyLocationId(locationId);
        return LocationIngredientDtoMapper.INSTANCE.fromEntity(locationIngredients);
    }

    public LocationIngredientDto getIngredientById(Long locationId, Long ingredientId) {
        Optional<LocationIngredient> locationIngredientOptional = locationIngredientRepository.findById(new LocationIngredientKey(locationId, ingredientId));
        if (locationIngredientOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found for id: " + ingredientId);
        }
        return LocationIngredientDtoMapper.INSTANCE.fromEntity(locationIngredientOptional.get());
    }

    public void applyQuantityChange(ChangeQuantityCommand changeQuantityCommand) {
        if (changeQuantityCommand.inventoryAuditLogs() == null || changeQuantityCommand.inventoryAuditLogs().isEmpty()) {
            return;
        }

        // Fetch fresh inventory numbers for this location
        List<LocationIngredient> locationIngredients = locationIngredientRepository.findByKeyLocationId(changeQuantityCommand.location().getId());

        // Apply the changes to the inventory from the audit logs
        changeQuantityCommand.inventoryAuditLogs().forEach(auditLog -> applyQuantityChangeToIngredient(auditLog, locationIngredients));

        locationIngredientRepository.saveAll(locationIngredients);
    }

    private void applyQuantityChangeToIngredient(InventoryAuditLogDto auditLog, List<LocationIngredient> locationIngredients) {
        Optional<LocationIngredient> locationIngredientOptional = locationIngredients.stream()
                .filter(locationIngredient -> locationIngredient.getKey().getIngredientId().equals(auditLog.getIngredientId()))
                .findFirst();
        if (locationIngredientOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found for id: " + auditLog.getIngredientId());
        }
        LocationIngredient locationIngredient = locationIngredientOptional.get();

        // Depending on the type of change, either subtract or add to the current quantity
        switch (auditLog.getQuantityChangeType()) {
            case DELIVERY, CORRECTION -> locationIngredient.setQuantity(locationIngredient.getQuantity().add(auditLog.getQuantityChangeAmount()));
            case SALE, WASTE -> locationIngredient.setQuantity(locationIngredient.getQuantity().subtract(auditLog.getQuantityChangeAmount()));
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported changeType: " + auditLog.getQuantityChangeType());
        }

        // Reject the request if any of the changes would place the ingredient quantity in the negative
        if (locationIngredient.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough in stock for ingredient: " + auditLog.getIngredientName());
        }

        locationIngredient.setUpdatedBy(auditLog.getStaffMemberName());
    }
}

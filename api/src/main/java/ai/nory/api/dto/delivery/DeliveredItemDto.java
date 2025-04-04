package ai.nory.api.dto.delivery;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DeliveredItemDto(@NotNull Long ingredientId, @NotNull BigDecimal quantityDelivered) { }

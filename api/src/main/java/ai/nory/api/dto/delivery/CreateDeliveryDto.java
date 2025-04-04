package ai.nory.api.dto.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateDeliveryDto(@NotEmpty @Valid List<DeliveredItemDto> deliveredItems) { }

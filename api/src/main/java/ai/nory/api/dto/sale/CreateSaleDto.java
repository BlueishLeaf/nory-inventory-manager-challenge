package ai.nory.api.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateSaleDto(@NotEmpty @Valid List<SoldItemDto> soldItems) { }

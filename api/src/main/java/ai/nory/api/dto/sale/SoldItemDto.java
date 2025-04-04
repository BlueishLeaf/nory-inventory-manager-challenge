package ai.nory.api.dto.sale;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record SoldItemDto(@NotNull Long recipeId, @NotNull Integer quantity, Set<Long> modifierIds) { }

package ai.nory.api.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientDto {
    private Long recipeId;
    private Long ingredientId;
    private IngredientDto ingredient;
    private BigDecimal quantity;
}

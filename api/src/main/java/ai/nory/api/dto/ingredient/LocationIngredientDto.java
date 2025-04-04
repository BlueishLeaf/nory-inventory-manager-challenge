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
public class LocationIngredientDto {
    private Long ingredientId;
    private Long locationId;
    private IngredientDto ingredient;
    private BigDecimal quantity;
}

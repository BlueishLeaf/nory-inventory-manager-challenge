package ai.nory.api.dto.menu;

import ai.nory.api.enumerator.ModifierCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDto {
    private Long recipeId;
    private Long locationId;
    private String recipeName;
    private ModifierCategory modifierType;
    private BigDecimal price;
}

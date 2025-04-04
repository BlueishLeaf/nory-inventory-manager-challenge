package ai.nory.api.dto.sale;

import ai.nory.api.dto.modifier.ModifierDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleItemDto {
    private Long id;
    private Long recipeId;
    private Long locationId;
    private String recipeName;
    private BigDecimal menuItemPrice;
    private List<ModifierDto> modifiers;
    private Integer quantity;
    private BigDecimal totalPrice;
}

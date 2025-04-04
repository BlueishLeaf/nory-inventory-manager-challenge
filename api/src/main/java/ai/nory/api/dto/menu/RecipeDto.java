package ai.nory.api.dto.menu;

import ai.nory.api.dto.ingredient.RecipeIngredientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private List<RecipeIngredientDto> ingredients;
}

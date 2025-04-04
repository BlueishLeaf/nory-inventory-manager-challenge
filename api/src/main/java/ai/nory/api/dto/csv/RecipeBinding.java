package ai.nory.api.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecipeBinding {
    @CsvBindByName(column = "recipe_id")
    private Long recipeId;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "quantity")
    private BigDecimal quantity;

    @CsvBindByName(column = "ingredient_id")
    private Long ingredientId;
}

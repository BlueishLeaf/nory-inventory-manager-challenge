package ai.nory.api.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredientBinding {
    @CsvBindByName(column = "ingredient_id")
    private Long ingredientId;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "unit")
    private String unit;

    @CsvBindByName(column = "cost")
    private BigDecimal cost;
}

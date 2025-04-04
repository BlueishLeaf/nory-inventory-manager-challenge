package ai.nory.api.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemBinding {
    @CsvBindByName(column = "recipe_id")
    private Long recipeId;

    @CsvBindByName(column = "location_id")
    private Long locationId;

    @CsvBindByName(column = "price")
    private BigDecimal price;

    @CsvBindByName(column = "modifiers")
    private Long modifierTypeId;
}

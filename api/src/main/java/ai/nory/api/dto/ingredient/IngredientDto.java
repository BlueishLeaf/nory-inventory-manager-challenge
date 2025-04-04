package ai.nory.api.dto.ingredient;

import ai.nory.api.enumerator.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDto {
    private Long id;
    private String name;
    private MeasurementUnit unit;
    private BigDecimal cost;
}

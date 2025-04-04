package ai.nory.api.dto.modifier;

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
public class ModifierDto {
    private Long id;
    private ModifierCategory modifierType;
    private String option;
    private BigDecimal price;
}

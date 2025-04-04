package ai.nory.api.dto.modifier;

import ai.nory.api.enumerator.ModifierCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifierTypeDto {
    private Long id;
    private ModifierCategory name;
}

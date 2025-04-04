package ai.nory.api.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MenuItemKey {
    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "location_id")
    private Long locationId;
}

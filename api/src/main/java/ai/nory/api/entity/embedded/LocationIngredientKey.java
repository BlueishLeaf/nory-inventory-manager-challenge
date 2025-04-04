package ai.nory.api.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LocationIngredientKey implements Serializable {
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "ingredient_id")
    private Long ingredientId;
}

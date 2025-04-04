package ai.nory.api.entity;

import ai.nory.api.entity.embedded.AuditableEntity;
import ai.nory.api.entity.embedded.MenuItemKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "menu_item", schema = "inventory_management")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class MenuItem extends AuditableEntity {
    @EmbeddedId
    private MenuItemKey key;

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "modifier_type_id")
    private ModifierType modifierType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}

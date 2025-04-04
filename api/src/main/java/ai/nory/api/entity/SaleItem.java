package ai.nory.api.entity;

import ai.nory.api.entity.embedded.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "sale_item", schema = "inventory_management")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SaleItem extends AuditableEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id", nullable = false),
            @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    })
    private MenuItem menuItem;

    @ManyToMany
    @JoinTable(
            name = "sale_item_modifiers",
            schema = "inventory_management",
            joinColumns = @JoinColumn(name = "sale_item_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "modifier_id", nullable = false)
    )
    private Set<Modifier> modifiers;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
}

package ai.nory.api.entity;

import ai.nory.api.entity.embedded.AuditableEntity;
import ai.nory.api.enumerator.QuantityChangeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@Table(name = "inventory_audit_log", schema = "inventory_management")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class InventoryAuditLog extends AuditableEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "staff_member_id", nullable = false)
    private Long staffMemberId;

    @Column(name = "staff_member_name", nullable = false)
    private String staffMemberName;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_change_type", nullable = false)
    private QuantityChangeType quantityChangeType;

    @Column(name = "quantity_before", nullable = false)
    private BigDecimal quantityBefore;

    @Column(name = "quantity_after", nullable = false)
    private BigDecimal quantityAfter;

    @Column(name = "quantity_change_amount", nullable = false)
    private BigDecimal quantityChangeAmount;

    @Column(name = "quantity_change_cost", nullable = false)
    private BigDecimal quantityChangeCost;
}

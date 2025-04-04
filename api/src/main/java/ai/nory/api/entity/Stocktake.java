package ai.nory.api.entity;

import ai.nory.api.entity.embedded.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stocktake", schema = "inventory_management")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Stocktake extends AuditableEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "staff_member_id", nullable = false)
    private Long staffMemberId;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "stocktake_audit_log",
            schema = "inventory_management",
            joinColumns = @JoinColumn(name = "stocktake_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "audit_log_id", nullable = false)
    )
    private Set<InventoryAuditLog> inventoryAuditLogs;
}

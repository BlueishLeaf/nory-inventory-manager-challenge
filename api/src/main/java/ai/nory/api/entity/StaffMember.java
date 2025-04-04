package ai.nory.api.entity;

import ai.nory.api.entity.embedded.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "staff_member", schema = "inventory_management")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class StaffMember extends AuditableEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "iban", nullable = false)
    private String iban;

    @Column(name = "bic", nullable = false)
    private String bic;

    @ManyToMany(mappedBy = "staffMembers")
    private Set<Location> locations;
}

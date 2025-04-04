package ai.nory.api.repository;

import ai.nory.api.entity.ModifierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifierTypeRepository extends JpaRepository<ModifierType, Long> {
}

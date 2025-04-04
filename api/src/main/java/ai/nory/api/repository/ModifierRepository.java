package ai.nory.api.repository;

import ai.nory.api.entity.Modifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifierRepository extends JpaRepository<Modifier, Long> {
}

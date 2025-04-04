package ai.nory.api.repository;

import ai.nory.api.entity.LocationIngredient;
import ai.nory.api.entity.embedded.LocationIngredientKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationIngredientRepository extends JpaRepository<LocationIngredient, LocationIngredientKey> {
    List<LocationIngredient> findByKeyLocationId(Long locationId);
}

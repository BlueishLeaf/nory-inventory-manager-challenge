package ai.nory.api.repository;

import ai.nory.api.entity.Stocktake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface StocktakeRepository extends JpaRepository<Stocktake, Long> {
    List<Stocktake> findByLocationIdAndCreatedAtBetween(Long locationId, Instant fromDate, Instant toDate);
}

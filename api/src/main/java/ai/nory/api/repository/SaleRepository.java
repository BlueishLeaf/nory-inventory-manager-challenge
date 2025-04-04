package ai.nory.api.repository;

import ai.nory.api.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByLocationIdAndCreatedAtBetween(Long locationId, Instant fromDate, Instant toDate);
}

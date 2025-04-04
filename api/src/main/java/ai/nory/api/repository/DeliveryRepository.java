package ai.nory.api.repository;

import ai.nory.api.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByLocationIdAndCreatedAtBetween(Long locationId, Instant fromDate, Instant toDate);
}

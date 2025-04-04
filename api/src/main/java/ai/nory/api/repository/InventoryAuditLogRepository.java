package ai.nory.api.repository;

import ai.nory.api.entity.InventoryAuditLog;
import ai.nory.api.enumerator.QuantityChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, Long> {
    List<InventoryAuditLog> findByLocationIdAndCreatedAtBetween(Long locationId, Instant fromDate, Instant toDate);

    List<InventoryAuditLog> findByLocationIdAndQuantityChangeTypeAndCreatedAtBetween(
            Long locationId,
            QuantityChangeType quantityChangeType,
            Instant fromDate,
            Instant toDate
    );
}

package ai.nory.api.service;

import ai.nory.api.dto.report.InventoryAuditLogDto;
import ai.nory.api.entity.InventoryAuditLog;
import ai.nory.api.mapper.InventoryAuditLogDtoMapper;
import ai.nory.api.repository.InventoryAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryAuditLogService {
    private final InventoryAuditLogRepository inventoryAuditLogRepository;

    public List<InventoryAuditLogDto> getInventoryAuditLogsForPeriod(Long locationId, Instant fromDate, Instant toDate) {
        List<InventoryAuditLog> inventoryAuditLogs = inventoryAuditLogRepository.findByLocationIdAndCreatedAtBetween(
                locationId,
                fromDate,
                toDate
        );

        return InventoryAuditLogDtoMapper.INSTANCE.fromEntity(inventoryAuditLogs);
    }
}

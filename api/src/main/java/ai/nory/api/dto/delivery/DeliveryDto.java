package ai.nory.api.dto.delivery;

import ai.nory.api.dto.report.InventoryAuditLogDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDto {
    private Long id;
    private Long locationId;
    private Long staffMemberId;
    private List<InventoryAuditLogDto> inventoryAuditLogs;
}

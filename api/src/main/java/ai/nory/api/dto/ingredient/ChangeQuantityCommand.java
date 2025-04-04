package ai.nory.api.dto.ingredient;

import ai.nory.api.dto.report.InventoryAuditLogDto;
import ai.nory.api.dto.LocationDto;

import java.util.List;

public record ChangeQuantityCommand(LocationDto location, List<InventoryAuditLogDto> inventoryAuditLogs) { }

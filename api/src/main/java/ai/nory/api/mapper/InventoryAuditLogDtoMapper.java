package ai.nory.api.mapper;

import ai.nory.api.dto.report.InventoryAuditLogDto;
import ai.nory.api.entity.InventoryAuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface InventoryAuditLogDtoMapper {
    InventoryAuditLogDtoMapper INSTANCE = Mappers.getMapper(InventoryAuditLogDtoMapper.class);

    InventoryAuditLogDto fromEntity(InventoryAuditLog inventoryAuditLog);

    List<InventoryAuditLogDto> fromEntity(List<InventoryAuditLog> inventoryAuditLogs);
}

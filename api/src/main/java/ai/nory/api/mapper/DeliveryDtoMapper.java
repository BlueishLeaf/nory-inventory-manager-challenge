package ai.nory.api.mapper;

import ai.nory.api.dto.delivery.DeliveryDto;
import ai.nory.api.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = InventoryAuditLogDtoMapper.class)
public interface DeliveryDtoMapper {
    DeliveryDtoMapper INSTANCE = Mappers.getMapper(DeliveryDtoMapper.class);

    DeliveryDto fromEntity(Delivery delivery);

    List<DeliveryDto> fromEntity(List<Delivery> deliveries);
}

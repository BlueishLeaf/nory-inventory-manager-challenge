package ai.nory.api.mapper;

import ai.nory.api.dto.stocktake.StocktakeDto;
import ai.nory.api.entity.Stocktake;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = InventoryAuditLogDtoMapper.class)
public interface StocktakeDtoMapper {
    StocktakeDtoMapper INSTANCE = Mappers.getMapper(StocktakeDtoMapper.class);

    StocktakeDto fromEntity(Stocktake stocktake);

    List<StocktakeDto> fromEntity(List<Stocktake> stocktakes);
}

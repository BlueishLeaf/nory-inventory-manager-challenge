package ai.nory.api.mapper;

import ai.nory.api.dto.sale.SaleDto;
import ai.nory.api.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = SaleItemDtoMapper.class)
public interface SaleDtoMapper {
    SaleDtoMapper INSTANCE = Mappers.getMapper(SaleDtoMapper.class);

    SaleDto fromEntity(Sale sale);

    List<SaleDto> fromEntity(List<Sale> sales);
}

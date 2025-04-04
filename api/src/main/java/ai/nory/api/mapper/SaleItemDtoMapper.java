package ai.nory.api.mapper;

import ai.nory.api.dto.sale.SaleItemDto;
import ai.nory.api.entity.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {MenuItemDtoMapper.class, ModifierDtoMapper.class})
public interface SaleItemDtoMapper {
    SaleItemDtoMapper INSTANCE = Mappers.getMapper(SaleItemDtoMapper.class);

    @Mapping(source = "menuItem.key.recipeId", target = "recipeId")
    @Mapping(source = "menuItem.key.locationId", target = "locationId")
    @Mapping(source = "menuItem.price", target = "menuItemPrice")
    @Mapping(source = "menuItem.recipe.name", target = "recipeName")
    SaleItemDto fromEntity(SaleItem saleItem);
}

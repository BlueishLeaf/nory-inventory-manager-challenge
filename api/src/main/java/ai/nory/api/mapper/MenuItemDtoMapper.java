package ai.nory.api.mapper;

import ai.nory.api.dto.menu.MenuItemDto;
import ai.nory.api.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ModifierTypeDtoMapper.class, RecipeDtoMapper.class})
public interface MenuItemDtoMapper {
    MenuItemDtoMapper INSTANCE = Mappers.getMapper(MenuItemDtoMapper.class);

    @Mapping(source = "key.recipeId", target = "recipeId")
    @Mapping(source = "key.locationId", target = "locationId")
    @Mapping(source = "modifierType.name", target = "modifierType")
    @Mapping(source = "recipe.name", target = "recipeName")
    MenuItemDto fromEntity(MenuItem menuItem);

    List<MenuItemDto> fromEntity(List<MenuItem> menuItems);
}

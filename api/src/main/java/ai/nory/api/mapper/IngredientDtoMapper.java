package ai.nory.api.mapper;

import ai.nory.api.dto.ingredient.IngredientDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IngredientDtoMapper {
    IngredientDtoMapper INSTANCE = Mappers.getMapper(IngredientDtoMapper.class);

    IngredientDto fromEntity(IngredientDto ingredientDto);
}

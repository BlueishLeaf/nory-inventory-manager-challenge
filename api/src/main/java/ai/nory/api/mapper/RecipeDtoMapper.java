package ai.nory.api.mapper;

import ai.nory.api.dto.menu.RecipeDto;
import ai.nory.api.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = RecipeIngredientDtoMapper.class)
public interface RecipeDtoMapper {
    RecipeDtoMapper INSTANCE = Mappers.getMapper(RecipeDtoMapper.class);

    RecipeDto fromEntity(Recipe recipe);
}

package ai.nory.api.mapper;

import ai.nory.api.dto.ingredient.RecipeIngredientDto;
import ai.nory.api.entity.RecipeIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = IngredientDtoMapper.class)
public interface RecipeIngredientDtoMapper {
    RecipeIngredientDtoMapper INSTANCE = Mappers.getMapper(RecipeIngredientDtoMapper.class);

    @Mapping(source = "key.ingredientId", target = "ingredientId")
    @Mapping(source = "key.recipeId", target = "recipeId")
    RecipeIngredientDto fromEntity(RecipeIngredient recipeIngredient);
}

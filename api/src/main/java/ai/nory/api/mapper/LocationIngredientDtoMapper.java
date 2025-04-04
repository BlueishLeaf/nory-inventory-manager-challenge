package ai.nory.api.mapper;

import ai.nory.api.dto.ingredient.LocationIngredientDto;
import ai.nory.api.entity.LocationIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LocationIngredientDtoMapper {
    LocationIngredientDtoMapper INSTANCE = Mappers.getMapper(LocationIngredientDtoMapper.class);

    @Mapping(source = "key.ingredientId", target = "ingredientId")
    @Mapping(source = "key.locationId", target = "locationId")
    LocationIngredientDto fromEntity(LocationIngredient locationIngredient);

    List<LocationIngredientDto> fromEntity(List<LocationIngredient> locationIngredients);
}

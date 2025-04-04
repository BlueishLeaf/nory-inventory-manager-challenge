package ai.nory.api.mapper;

import ai.nory.api.dto.modifier.ModifierTypeDto;
import ai.nory.api.entity.ModifierType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ModifierTypeDtoMapper {
    ModifierTypeDtoMapper INSTANCE = Mappers.getMapper(ModifierTypeDtoMapper.class);

    ModifierTypeDto fromEntity(ModifierType modifierType);
}

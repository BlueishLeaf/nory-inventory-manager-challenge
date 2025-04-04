package ai.nory.api.mapper;

import ai.nory.api.dto.modifier.ModifierDto;
import ai.nory.api.entity.Modifier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ModifierDtoMapper {
    ModifierDtoMapper INSTANCE = Mappers.getMapper(ModifierDtoMapper.class);

    @Mapping(source = "modifierType.name", target = "modifierType")
    ModifierDto fromEntity(Modifier modifier);

    List<ModifierDto> fromEntity(List<Modifier> modifier);
}

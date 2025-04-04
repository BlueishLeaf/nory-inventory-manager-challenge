package ai.nory.api.mapper;

import ai.nory.api.dto.LocationDto;
import ai.nory.api.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LocationDtoMapper {
    LocationDtoMapper INSTANCE = Mappers.getMapper(LocationDtoMapper.class);

    LocationDto fromEntity(Location location);
}

package ai.nory.api.mapper;

import ai.nory.api.dto.StaffMemberDto;
import ai.nory.api.entity.StaffMember;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = LocationDtoMapper.class)
public interface StaffMemberDtoMapper {
    StaffMemberDtoMapper INSTANCE = Mappers.getMapper(StaffMemberDtoMapper.class);

    StaffMemberDto fromEntity(StaffMember staffMember);
}

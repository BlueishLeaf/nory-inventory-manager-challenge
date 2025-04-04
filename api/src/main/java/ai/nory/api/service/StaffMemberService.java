package ai.nory.api.service;

import ai.nory.api.dto.StaffMemberDto;
import ai.nory.api.entity.StaffMember;
import ai.nory.api.mapper.StaffMemberDtoMapper;
import ai.nory.api.repository.StaffMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffMemberService {
    private final StaffMemberRepository staffMemberRepository;

    public StaffMemberDto getStaffMember(Long staffMemberId) {
        Optional<StaffMember> staffMember = staffMemberRepository.findById(staffMemberId);
        if (staffMember.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff member not found for id: " + staffMemberId);
        }

        return StaffMemberDtoMapper.INSTANCE.fromEntity(staffMember.get());
    }
}

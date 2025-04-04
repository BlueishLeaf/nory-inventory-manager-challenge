package ai.nory.api.controller;

import ai.nory.api.dto.LocationDto;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.StaffMemberDto;
import ai.nory.api.service.StaffMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("identities")
@Transactional
@RequiredArgsConstructor
public class IdentityController {
    private final StaffMemberService staffMemberService;

    @GetMapping("/{staffMemberId}")
    public StaffIdentity getStaffIdentity(@PathVariable Long staffMemberId) {
        StaffMemberDto staffMemberDto = staffMemberService.getStaffMember(staffMemberId);
        LocationDto locationDto = staffMemberDto.getLocations().getFirst(); // Only 1 location will be loaded so this is fine for now
        return new StaffIdentity(locationDto, staffMemberDto);
    }
}

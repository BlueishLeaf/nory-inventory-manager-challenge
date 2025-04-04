package ai.nory.api.identity;

import ai.nory.api.constant.RoleConstant;
import ai.nory.api.dto.LocationDto;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.StaffMemberDto;
import ai.nory.api.service.LocationService;
import ai.nory.api.service.StaffMemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class IdentityHelper {
    private final LocationService locationService;
    private final StaffMemberService staffMemberService;
    private final Logger log = LoggerFactory.getLogger(IdentityHelper.class);

    public StaffIdentity validateStaffIdentity(IdentityHeaders identityHeaders, Set<String> allowedRoles) {
        log.info("Validating staff identity: {}", identityHeaders.toString());
        if (identityHeaders.getStaffMemberId() == null || identityHeaders.getLocationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required headers: location_id, staff_member_id");
        }

        LocationDto locationDto = locationService.getLocation(identityHeaders.getLocationId());
        StaffMemberDto staffMemberDto = staffMemberService.getStaffMember(identityHeaders.getStaffMemberId());

        // Ensure staff member has a valid role for this endpoint
        if (!allowedRoles.contains(staffMemberDto.getRole()) && !allowedRoles.contains(RoleConstant.ROLE_ALL)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Staff member does not have the required role");
        }

        // Ensure staff member belongs to this location
        if (!staffMemberHasLocation(staffMemberDto, locationDto.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Staff member does not belong to this location");
        }

        return new StaffIdentity(locationDto, staffMemberDto);
    }

    private boolean staffMemberHasLocation(StaffMemberDto staffMemberDto, Long locationId) {
        return staffMemberDto.getLocations().stream().anyMatch(location -> location.getId().equals(locationId));
    }
}

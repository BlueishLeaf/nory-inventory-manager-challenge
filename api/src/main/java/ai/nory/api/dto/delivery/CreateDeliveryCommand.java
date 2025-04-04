package ai.nory.api.dto.delivery;

import ai.nory.api.dto.LocationDto;
import ai.nory.api.dto.StaffMemberDto;

public record CreateDeliveryCommand(
        LocationDto locationDto,
        StaffMemberDto staffMemberDto,
        CreateDeliveryDto createDeliveryDto
) { }

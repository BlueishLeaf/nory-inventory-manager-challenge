package ai.nory.api.dto.sale;

import ai.nory.api.dto.LocationDto;
import ai.nory.api.dto.StaffMemberDto;

public record CreateSaleCommand(
        LocationDto locationDto,
        StaffMemberDto staffMemberDto,
        CreateSaleDto createSaleDto
) { }

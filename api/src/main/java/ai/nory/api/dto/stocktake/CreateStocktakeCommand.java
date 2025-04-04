package ai.nory.api.dto.stocktake;

import ai.nory.api.dto.LocationDto;
import ai.nory.api.dto.StaffMemberDto;

public record CreateStocktakeCommand(LocationDto locationDto, StaffMemberDto staffMemberDto, CreateStocktakeDto createStocktakeDto) { }

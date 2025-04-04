package ai.nory.api.dto.report;


import ai.nory.api.dto.LocationDto;

public record GenerateReportCommand(LocationDto locationDto, ReportCriteriaDto reportCriteriaDto) { }

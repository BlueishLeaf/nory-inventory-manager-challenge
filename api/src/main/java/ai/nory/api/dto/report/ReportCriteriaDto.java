package ai.nory.api.dto.report;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReportCriteriaDto(@NotNull Instant fromDate, @NotNull Instant toDate) { }

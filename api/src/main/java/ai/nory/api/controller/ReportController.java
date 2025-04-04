package ai.nory.api.controller;

import ai.nory.api.constant.RoleConstant;
import ai.nory.api.dto.report.InventoryAuditLogDto;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.report.ReportCriteriaDto;
import ai.nory.api.identity.IdentityHeaders;
import ai.nory.api.identity.IdentityHelper;
import ai.nory.api.service.InventoryAuditLogService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@Transactional
@RequiredArgsConstructor
@RequestMapping("reporting")
public class ReportController {
    private final IdentityHeaders identityHeaders;
    private final IdentityHelper identityHelper;
    private final Set<String> allowedRoles = Set.of(RoleConstant.ROLE_MANAGER);

    private final InventoryAuditLogService inventoryAuditLogService;

    @PostMapping("/audit-logs")
    public List<InventoryAuditLogDto> fetchAuditLogsForPeriod(@RequestBody @Valid ReportCriteriaDto reportCriteriaDto) {
        StaffIdentity staffIdentity = identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        return inventoryAuditLogService.getInventoryAuditLogsForPeriod(staffIdentity.location().getId(), reportCriteriaDto.fromDate(), reportCriteriaDto.toDate());
    }
}

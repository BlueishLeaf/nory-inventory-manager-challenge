package ai.nory.api.controller;

import ai.nory.api.constant.RoleConstant;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.sale.CreateSaleCommand;
import ai.nory.api.dto.sale.CreateSaleDto;
import ai.nory.api.dto.sale.SaleDto;
import ai.nory.api.identity.IdentityHeaders;
import ai.nory.api.identity.IdentityHelper;
import ai.nory.api.service.SaleService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Transactional
@RequiredArgsConstructor
@RequestMapping("sales")
public class SaleController {
    private final IdentityHeaders identityHeaders;
    private final IdentityHelper identityHelper;
    private final Set<String> allowedRoles = Set.of(RoleConstant.ROLE_FRONT_HOUSE, RoleConstant.ROLE_MANAGER);

    private final SaleService saleService;

    @PostMapping
    public SaleDto createSale(@RequestBody @Valid CreateSaleDto createSaleDto) {
        StaffIdentity staffIdentity = identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        CreateSaleCommand createSaleCommand = new CreateSaleCommand(staffIdentity.location(), staffIdentity.staffMember(), createSaleDto);
        return saleService.createSale(createSaleCommand);
    }
}

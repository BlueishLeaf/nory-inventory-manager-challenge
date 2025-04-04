package ai.nory.api.controller;

import ai.nory.api.constant.RoleConstant;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.ingredient.LocationIngredientDto;
import ai.nory.api.identity.IdentityHeaders;
import ai.nory.api.identity.IdentityHelper;
import ai.nory.api.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("inventory")
public class InventoryController {
    private final IdentityHeaders identityHeaders;
    private final IdentityHelper identityHelper;
    private final Set<String> allowedRoles = Set.of(RoleConstant.ROLE_ALL);

    private final IngredientService ingredientService;

    @GetMapping("/ingredients")
    public List<LocationIngredientDto> fetchIngredients() {
        StaffIdentity staffIdentity = identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        return ingredientService.getIngredients(staffIdentity.location().getId());
    }

    @GetMapping("/ingredients/{id}")
    public LocationIngredientDto fetchIngredientById(@PathVariable Long id) {
        StaffIdentity staffIdentity = identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        return ingredientService.getIngredientById(staffIdentity.location().getId(), id);
    }
}

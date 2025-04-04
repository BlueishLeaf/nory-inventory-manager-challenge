package ai.nory.api.controller;

import ai.nory.api.constant.RoleConstant;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.menu.MenuItemDto;
import ai.nory.api.dto.modifier.ModifierDto;
import ai.nory.api.identity.IdentityHeaders;
import ai.nory.api.identity.IdentityHelper;
import ai.nory.api.service.MenuService;
import ai.nory.api.service.ModifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("menu")
public class MenuController {
    private final IdentityHeaders identityHeaders;
    private final IdentityHelper identityHelper;
    private final Set<String> allowedRoles = Set.of(RoleConstant.ROLE_ALL);

    private final MenuService menuService;
    private final ModifierService modifierService;

    @GetMapping("/items")
    public List<MenuItemDto> fetchMenuItems() {
        StaffIdentity staffIdentity = identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        return menuService.getMenuItems(staffIdentity.location().getId());
    }

    @GetMapping("/modifiers")
    public List<ModifierDto> fetchModifiers() {
        identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        return modifierService.getModifiers();
    }
}

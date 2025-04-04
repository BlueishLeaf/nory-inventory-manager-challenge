package ai.nory.api.controller;

import ai.nory.api.constant.RoleConstant;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.delivery.CreateDeliveryCommand;
import ai.nory.api.dto.delivery.CreateDeliveryDto;
import ai.nory.api.dto.delivery.DeliveredItemDto;
import ai.nory.api.dto.delivery.DeliveryDto;
import ai.nory.api.identity.IdentityHeaders;
import ai.nory.api.identity.IdentityHelper;
import ai.nory.api.service.DeliveryService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("deliveries")
@Transactional
@RequiredArgsConstructor
public class DeliveryController {
    private final IdentityHeaders identityHeaders;
    private final IdentityHelper identityHelper;
    private final Set<String> allowedRoles = Set.of(RoleConstant.ROLE_BACK_HOUSE, RoleConstant.ROLE_CHEF, RoleConstant.ROLE_MANAGER);

    private final DeliveryService deliveryService;

    @PostMapping
    public DeliveryDto createDelivery(@Valid @RequestBody CreateDeliveryDto createDeliveryDto) {
        StaffIdentity staffIdentity = identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        // Reject the request if there are duplicates of the same ingredient in the delivered items
        if (containsDuplicateDeliveredItems(createDeliveryDto.deliveredItems())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate deliveries in request");
        }

        CreateDeliveryCommand createDeliveryCommand = new CreateDeliveryCommand(staffIdentity.location(), staffIdentity.staffMember(), createDeliveryDto);
        return deliveryService.createDelivery(createDeliveryCommand);
    }

    private boolean containsDuplicateDeliveredItems(List<DeliveredItemDto> deliveredItems) {
        List<Long> ingredientIds = deliveredItems.stream().map(DeliveredItemDto::ingredientId).toList();
        return ingredientIds.stream().anyMatch(id -> Collections.frequency(ingredientIds, id) > 1);
    }
}

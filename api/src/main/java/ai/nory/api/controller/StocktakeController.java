package ai.nory.api.controller;

import ai.nory.api.constant.RoleConstant;
import ai.nory.api.dto.StaffIdentity;
import ai.nory.api.dto.stocktake.CreateStocktakeCommand;
import ai.nory.api.dto.stocktake.CreateStocktakeDto;
import ai.nory.api.dto.stocktake.StockCorrectionDto;
import ai.nory.api.dto.stocktake.StocktakeDto;
import ai.nory.api.identity.IdentityHeaders;
import ai.nory.api.identity.IdentityHelper;
import ai.nory.api.service.StocktakeService;
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
@Transactional
@RequiredArgsConstructor
@RequestMapping("stocktakes")
public class StocktakeController {
    private final IdentityHeaders identityHeaders;
    private final IdentityHelper identityHelper;
    private final Set<String> allowedRoles = Set.of(RoleConstant.ROLE_ALL);

    private final StocktakeService stocktakeService;

    @PostMapping
    public StocktakeDto create(@RequestBody @Valid CreateStocktakeDto createStocktakeDto) {
        StaffIdentity staffIdentity = identityHelper.validateStaffIdentity(identityHeaders, allowedRoles);

        // Reject the request if there are duplicates of the same ingredient in the stock corrections
        if (containsDuplicateStockCorrectionItems(createStocktakeDto.stockCorrections())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate stock corrections in request");
        }

        CreateStocktakeCommand createStocktakeCommand = new CreateStocktakeCommand(staffIdentity.location(), staffIdentity.staffMember(), createStocktakeDto);
        return stocktakeService.createStocktake(createStocktakeCommand);
    }

    private boolean containsDuplicateStockCorrectionItems(List<StockCorrectionDto> stockCorrections) {
        List<Long> ingredientIds = stockCorrections.stream().map(StockCorrectionDto::ingredientId).toList();
        return ingredientIds.stream().anyMatch(id -> Collections.frequency(ingredientIds, id) > 1);
    }
}

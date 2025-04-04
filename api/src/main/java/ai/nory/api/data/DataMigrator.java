package ai.nory.api.data;

import ai.nory.api.constant.MigrationConstant;
import ai.nory.api.dto.csv.IngredientBinding;
import ai.nory.api.dto.csv.LocationBinding;
import ai.nory.api.dto.csv.MenuItemBinding;
import ai.nory.api.dto.csv.ModifierBinding;
import ai.nory.api.dto.csv.RecipeBinding;
import ai.nory.api.dto.csv.StaffMemberBinding;
import ai.nory.api.entity.Ingredient;
import ai.nory.api.entity.Location;
import ai.nory.api.entity.LocationIngredient;
import ai.nory.api.entity.MenuItem;
import ai.nory.api.entity.Modifier;
import ai.nory.api.entity.ModifierType;
import ai.nory.api.entity.Recipe;
import ai.nory.api.entity.RecipeIngredient;
import ai.nory.api.entity.StaffMember;
import ai.nory.api.entity.embedded.LocationIngredientKey;
import ai.nory.api.entity.embedded.MenuItemKey;
import ai.nory.api.entity.embedded.RecipeIngredientKey;
import ai.nory.api.enumerator.MeasurementUnit;
import ai.nory.api.enumerator.ModifierCategory;
import ai.nory.api.repository.IngredientRepository;
import ai.nory.api.repository.LocationRepository;
import ai.nory.api.repository.MenuItemRepository;
import ai.nory.api.repository.ModifierRepository;
import ai.nory.api.repository.ModifierTypeRepository;
import ai.nory.api.repository.RecipeRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
@RequiredArgsConstructor
public class DataMigrator implements ApplicationListener<ContextRefreshedEvent> {
    private final static Logger log = LoggerFactory.getLogger(DataMigrator.class);

    private final Environment environment;

    private final LocationRepository locationRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;
    private final ModifierTypeRepository modifierTypeRepository;
    private final ModifierRepository modifierRepository;
    private final RecipeRepository recipeRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Application initialized. Checking if brand data has been migrated...");

        String locationIdEnv = environment.getProperty(MigrationConstant.LOCATION_ID_ENV_VAR);
        if (locationIdEnv == null) {
            log.info("ACTIVE_LOCATION_ID env variable not provided, skipping migration.");
            return;
        }

        try {
            Long locationId = Long.valueOf(locationIdEnv);

            Optional<Location> locationOptional = locationRepository.findById(locationId);
            if (locationOptional.isPresent()) {
                log.info("Brand data has already been migrated for location id: {}, skipping migration.",locationId);
                return;
            }

            log.info("Brand data has not yet been migrated for location id: {}, performing migration...", locationId);
            migrateBrandData(locationId);
            log.info("Brand data has been successfully migrated for location id: {}.", locationId);
        } catch (Exception e) {
            log.error("Failed to migrate brand data for location id: {}.", locationIdEnv, e);
        }
    }

    private void migrateBrandData(Long locationId) {
        List<IngredientBinding> ingredientBindings = parseCsv("data/ingredients.csv", IngredientBinding.class);
        persistIngredients(ingredientBindings);

        List<LocationBinding> locationBindings = parseCsv("data/locations.csv", LocationBinding.class);
        List<StaffMemberBinding> staffMemberBindings = parseCsv("data/staff.csv", StaffMemberBinding.class);
        persistLocations(locationId, locationBindings, staffMemberBindings, ingredientBindings);

        List<ModifierBinding> modifierBindings = parseCsv("data/modifiers.csv", ModifierBinding.class);
        persistModifiers(modifierBindings);

        List<RecipeBinding> recipeBindings = parseCsv("data/recipes.csv", RecipeBinding.class);
        persistRecipes(recipeBindings);

        List<MenuItemBinding> menuItemBindings = parseCsv("data/menus.csv", MenuItemBinding.class);
        persistMenuItems(locationId, menuItemBindings);
    }

    private void persistMenuItems(Long locationId, List<MenuItemBinding> menuItemBindings) {
        log.info("Persisting menu items...");
        List<MenuItem> menuItems = menuItemBindings.stream()
                .filter(menuItemBinding -> menuItemBinding.getLocationId().equals(locationId))
                .map(menuItemBinding -> MenuItem.builder()
                        .key(new MenuItemKey(menuItemBinding.getRecipeId(), menuItemBinding.getLocationId()))
                        .recipe(recipeRepository.getReferenceById(menuItemBinding.getRecipeId()))
                        .modifierType(menuItemBinding.getModifierTypeId() != null ? modifierTypeRepository.getReferenceById(menuItemBinding.getModifierTypeId()) : null)
                        .price(menuItemBinding.getPrice())
                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                        .createdAt(Instant.now())
                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                        .updatedAt(Instant.now())
                        .build())
                .collect(Collectors.toList());
        menuItemRepository.saveAll(menuItems);
        log.info("Successfully persisted menu items.");
    }

    private void persistRecipes(List<RecipeBinding> recipeBindings) {
        log.info("Persisting recipes...");

        Map<Long, List<RecipeBinding>> recipeBindingIngredientMap = recipeBindings.stream()
                .collect(groupingBy(RecipeBinding::getRecipeId));

        List<Recipe> recipes = new ArrayList<>();
        recipeBindingIngredientMap.forEach((recipeId, ingredientRecipeBindings) ->
                recipes.add(Recipe.builder()
                        .id(recipeId)
                        .name(ingredientRecipeBindings.getFirst().getName())
                        .ingredients(ingredientRecipeBindings.stream()
                                .map(recipeBinding -> RecipeIngredient.builder()
                                        .key(new RecipeIngredientKey(recipeId, recipeBinding.getIngredientId()))
                                        .quantity(recipeBinding.getQuantity())
                                        .ingredient(ingredientRepository.getReferenceById(recipeBinding.getIngredientId()))
                                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                                        .createdAt(Instant.now())
                                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                                        .updatedAt(Instant.now())
                                        .build())
                                .collect(Collectors.toSet()))
                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                        .createdAt(Instant.now())
                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                        .updatedAt(Instant.now())
                .build()));
        recipeRepository.saveAll(recipes);
        log.info("Successfully persisted recipes.");
    }

    private void persistModifiers(List<ModifierBinding> modifierBindings) {
        log.info("Persisting modifiers...");
        Map<Long, List<ModifierBinding>> modifierTypesMap = modifierBindings.stream()
                .collect(groupingBy(ModifierBinding::getModifierTypeId));

        List<ModifierType> modifierTypes = new ArrayList<>();
        modifierTypesMap.forEach((typeId, typeModifierBindings) ->
                modifierTypes.add(ModifierType.builder()
                        .id(typeId)
                        .name(ModifierCategory.fromCsvBinding(typeModifierBindings.getFirst().getModifierTypeName()))
                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                        .createdAt(Instant.now())
                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                        .updatedAt(Instant.now())
                .build()));
        modifierTypeRepository.saveAll(modifierTypes);

        List<Modifier> modifiers = new ArrayList<>();
        modifierTypesMap.forEach((typeId, typeModifierBindings) -> typeModifierBindings.forEach(modifierBinding ->
                modifiers.add(Modifier.builder()
                        .option(modifierBinding.getOption())
                        .price(modifierBinding.getPrice())
                        .modifierType(modifierTypeRepository.getReferenceById(typeId))
                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                        .createdAt(Instant.now())
                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                        .updatedAt(Instant.now())
                        .build())));
        modifierRepository.saveAll(modifiers);

        log.info("Successfully persisted modifiers.");
    }

    private void persistIngredients(List<IngredientBinding> ingredientBindings) {
        log.info("Persisting ingredients...");
        List<Ingredient> ingredients = ingredientBindings.stream()
                .map(ingredientBinding -> Ingredient.builder()
                        .id(ingredientBinding.getIngredientId())
                        .name(ingredientBinding.getName())
                        .unit(MeasurementUnit.valueOf(ingredientBinding.getUnit().toUpperCase()))
                        .cost(ingredientBinding.getCost())
                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                        .createdAt(Instant.now())
                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                        .updatedAt(Instant.now())
                        .build()
                ).collect(Collectors.toList());
        ingredientRepository.saveAll(ingredients);
        log.info("Successfully persisted ingredients.");
    }

    private void persistLocations(Long locationId, List<LocationBinding> locationBindings, List<StaffMemberBinding> staffMemberBindings, List<IngredientBinding> ingredientBindings) {
        log.info("Persisting locations...");
        List<Location> locations = locationBindings.stream()
                .filter(locationBinding -> locationBinding.getLocationId().equals(locationId))
                .map(locationBinding -> Location.builder()
                        .id(locationBinding.getLocationId())
                        .name(locationBinding.getName())
                        .address(locationBinding.getAddress())
                        .staffMembers(staffMemberBindings.stream()
                                .filter(staffMemberBinding -> staffMemberBinding.getLocationId().equals(locationBinding.getLocationId()))
                                .map(staffMemberBinding -> StaffMember.builder()
                                        .id(staffMemberBinding.getStaffMemberId())
                                        .name(staffMemberBinding.getName())
                                        .dateOfBirth(Date.valueOf(staffMemberBinding.getDateOfBirth()))
                                        .role(staffMemberBinding.getRole())
                                        .iban(staffMemberBinding.getIban())
                                        .bic(staffMemberBinding.getBic())
                                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                                        .createdAt(Instant.now())
                                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                                        .updatedAt(Instant.now())
                                        .build())
                                .collect(Collectors.toSet()))
                        .locationIngredients(ingredientBindings.stream()
                                .map(ingredientBinding -> LocationIngredient.builder()
                                        .key(new LocationIngredientKey(locationBinding.getLocationId(), ingredientBinding.getIngredientId()))
                                        .quantity(MigrationConstant.STARTING_QUANTITY) // Give a starting value for each location
                                        .ingredient(ingredientRepository.getReferenceById(ingredientBinding.getIngredientId()))
                                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                                        .createdAt(Instant.now())
                                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                                        .updatedAt(Instant.now())
                                        .build())
                                .collect(Collectors.toSet()))
                        .createdBy(MigrationConstant.SYSTEM_AUTHOR)
                        .createdAt(Instant.now())
                        .updatedBy(MigrationConstant.SYSTEM_AUTHOR)
                        .updatedAt(Instant.now())
                        .build()
                ).collect(Collectors.toList());
        locationRepository.saveAll(locations);
        log.info("Successfully persisted locations.");
    }

    private <T> List<T> parseCsv(String csvPath, Class<T> clazz) {
        log.info("Parsing data from {}...", csvPath);
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(csvPath);
        if (resource == null) {
            throw new RuntimeException("Unable to find file: " + csvPath);
        }

        Reader reader = new BufferedReader(new InputStreamReader(resource));
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                .withType(clazz)
                .build();

        List<T> bindings = csvToBean.parse();
        log.info("Successfully parsed data from {}: {}", csvPath, bindings);
        return bindings;
    }
}

package ai.nory.api.enumerator;

import lombok.Getter;

@Getter
public enum ModifierCategory {
    ADDED_INGREDIENT("Add ingredient"),
    ALLERGEN("Allergens");

    private final String csvBinding;

    ModifierCategory(String csvBinding) {
        this.csvBinding = csvBinding;
    }

    public static ModifierCategory fromCsvBinding(String csvBinding) {
        for (ModifierCategory modifierCategory : ModifierCategory.values()) {
            if (modifierCategory.csvBinding.equals(csvBinding)) {
                return modifierCategory;
            }
        }
        throw new IllegalArgumentException("Unknown modifier category: " + csvBinding);
    }

}

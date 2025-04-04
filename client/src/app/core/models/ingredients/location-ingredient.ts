import {Ingredient} from './ingredient';

export interface LocationIngredient {
  ingredientId: number;
  locationId: number;
  ingredient: Ingredient;
  quantity: number;
}

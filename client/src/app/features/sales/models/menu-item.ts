import {ModifierType} from './modifier-type';

export interface MenuItem {
  recipeId: number;
  locationId: number;
  recipeName: string;
  modifierType: ModifierType;
  price: number;
}

import {Modifier} from './modifier';

export interface SaleItem {
  id: number;
  recipeId: number;
  locationId: number;
  recipeName: string;
  menuItemPrice: number;
  modifiers: Modifier[];
  quantity: number;
  totalPrice: number;
}

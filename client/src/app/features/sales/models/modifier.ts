import {ModifierType} from './modifier-type';

export interface Modifier {
  id: number;
  modifierType: ModifierType;
  option: string;
  price: number;
}

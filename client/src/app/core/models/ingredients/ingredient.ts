import {MeasurementUnit} from './measurement-unit';

export interface Ingredient {
  id: number;
  name: string;
  unit: MeasurementUnit;
  cost: number;
}

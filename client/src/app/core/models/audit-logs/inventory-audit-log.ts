import {QuantityChangeType} from './quantity-change-type';

export interface InventoryAuditLog {
  id: number;
  locationId: number;
  staffMemberId: number;
  staffMemberName: string;
  ingredientId: number;
  ingredientName: string;
  quantityChangeType: QuantityChangeType;
  quantityBefore: number;
  quantityAfter: number;
  quantityChangeAmount: number;
  quantityChangeCost: number;
  createdAt: Date;
}

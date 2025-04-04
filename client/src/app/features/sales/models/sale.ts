import {SaleItem} from './sale-item';
import {InventoryAuditLog} from '../../../core/models/audit-logs/inventory-audit-log';

export interface Sale {
  id: number;
  locationId: number;
  staffMemberId: number;
  saleItems: SaleItem[];
  inventoryAuditLogs: InventoryAuditLog[];
}

import {InventoryAuditLog} from '../../../core/models/audit-logs/inventory-audit-log';

export interface Delivery {
  id: number;
  locationId: number;
  staffMemberId: number;
  inventoryAuditLogs: InventoryAuditLog[];
}

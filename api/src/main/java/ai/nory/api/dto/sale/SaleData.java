package ai.nory.api.dto.sale;

import ai.nory.api.entity.InventoryAuditLog;
import ai.nory.api.entity.SaleItem;

import java.util.List;
import java.util.Set;

public record SaleData (List<SaleItem> saleItems, Set<InventoryAuditLog> inventoryAuditLogs) { }

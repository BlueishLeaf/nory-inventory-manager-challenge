package ai.nory.api.dto;

import ai.nory.api.enumerator.QuantityChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryAuditLogDto {
    private Long id;
    private Long locationId;
    private Long staffMemberId;
    private String staffMemberName;
    private Long ingredientId;
    private String ingredientName;
    private QuantityChangeType quantityChangeType;
    private BigDecimal quantityBefore;
    private BigDecimal quantityAfter;
    private BigDecimal quantityChangeAmount;
    private BigDecimal quantityChangeCost;
    private Instant createdAt;
}

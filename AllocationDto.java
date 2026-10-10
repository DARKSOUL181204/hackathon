package com.defense.logistics.request.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AllocationDto {
    @NotNull private Long requestItemId;
    private Long resourceId;
    private Long supplyItemId;
    @NotNull @Positive private BigDecimal quantityAllocated;
    private String allocatedBy;
    // (In a real scenario, this would have target locations for supplies, etc.)
}

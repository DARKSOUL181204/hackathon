package com.defense.logistics.inventory.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockReceiveRequest {
    @NotNull private Long supplyItemId;
    @NotBlank private String location;
    @NotNull @Positive private BigDecimal quantity;
    @NotBlank private String reference;
}

package com.defense.logistics.inventory.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockDto {
    private Long id;
    private Long supplyItemId;
    private String itemCode;
    private String location;
    private BigDecimal quantityOnHand;
}

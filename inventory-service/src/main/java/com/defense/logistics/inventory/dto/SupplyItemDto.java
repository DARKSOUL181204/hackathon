package com.defense.logistics.inventory.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SupplyItemDto {
    private Long id;
    @NotBlank private String itemCode;
    @NotBlank private String name;
    @NotBlank private String category;
    @NotBlank private String unitOfMeasure;
    private String description;
    @NotNull @PositiveOrZero private BigDecimal reorderLevel;
    private boolean active;
}

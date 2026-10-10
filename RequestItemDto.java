package com.defense.logistics.request.dto;
import com.defense.logistics.request.model.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RequestItemDto {
    private Long id;
    @NotNull private ResourceType resourceType;
    private Long supplyItemId;
    @NotBlank private String description;
    @NotNull @Positive private BigDecimal quantityRequested;
    private String unitOfMeasure;
    private String specifications;
}

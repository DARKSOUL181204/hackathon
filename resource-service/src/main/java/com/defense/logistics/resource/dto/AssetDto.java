package com.defense.logistics.resource.dto;
import com.defense.logistics.resource.model.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetDto {
    private Long id;
    @NotBlank private String assetCode;
    @NotBlank private String name;
    @NotNull private AssetCategory category;
    @NotNull private Long unitId;
    @NotBlank private String location;
    @NotNull private OperationalStatus operationalStatus;
    @NotNull private ConditionStatus condition;
    private Long assignedPersonnelId;
}

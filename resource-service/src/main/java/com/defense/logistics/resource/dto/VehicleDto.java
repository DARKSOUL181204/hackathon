package com.defense.logistics.resource.dto;
import com.defense.logistics.resource.model.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleDto {
    private Long id;
    @NotBlank private String registrationNumber;
    @NotNull private VehicleType vehicleType;
    @NotNull private Long unitId;
    @NotBlank private String location;
    @NotNull private OperationalStatus operationalStatus;
    @NotNull private ConditionStatus condition;
    private Long driverPersonnelId;
}

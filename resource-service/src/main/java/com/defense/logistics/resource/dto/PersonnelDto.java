package com.defense.logistics.resource.dto;
import com.defense.logistics.resource.model.enums.AvailabilityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonnelDto {
    private Long id;
    @NotBlank private String serviceNumber;
    @NotBlank private String fullName;
    @NotBlank private String role;
    @NotNull private Long unitId;
    @NotBlank private String posting;
    @NotNull private AvailabilityStatus availabilityStatus;
    private String contactDetails;
}

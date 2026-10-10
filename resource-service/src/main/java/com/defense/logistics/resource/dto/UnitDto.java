package com.defense.logistics.resource.dto;
import com.defense.logistics.resource.model.enums.UnitStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnitDto {
    private Long id;
    @NotBlank private String name;
    @NotBlank private String code;
    @NotBlank private String location;
    @NotNull private UnitStatus status;
}

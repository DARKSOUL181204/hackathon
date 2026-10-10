package com.defense.logistics.resource.dto;
import com.defense.logistics.resource.model.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MaintenanceOrderDto {
    private Long id;
    @NotBlank private String maintenanceCode;
    @NotNull private MaintenanceTargetType targetType;
    private Long assetId;
    private Long vehicleId;
    @NotBlank private String description;
    @NotBlank private String priority;
    @NotNull private MaintenanceStatus status;
    private LocalDateTime scheduledDate;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}

package com.defense.logistics.request.dto;
import com.defense.logistics.request.model.enums.RequestStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LogisticsRequestDto {
    private Long id;
    private String requestNumber;
    @NotNull private Long requestingUnitId;
    @NotBlank private String requestedBy;
    private String purpose;
    @NotBlank private String priority;
    private LocalDateTime requiredByDate;
    private String deliveryLocation;
    private RequestStatus status;
    @NotEmpty @Valid private List<RequestItemDto> items;
}

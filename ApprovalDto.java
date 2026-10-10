package com.defense.logistics.request.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApprovalDto {
    @NotBlank private String approverId;
    @NotBlank private String decision; // APPROVED or REJECTED
    private String comments;
}

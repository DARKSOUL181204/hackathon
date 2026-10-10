package com.defense.logistics.resource.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class AccessRequestView {
    private Long id;
    private String fullName;
    private String username;
    private String requestedRole;
    private String requestDetails;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime decidedAt;
    private String decidedBy;
    private String rejectionReason;
}

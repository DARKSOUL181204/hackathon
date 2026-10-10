package com.defense.logistics.resource.model;

import com.defense.logistics.resource.model.enums.AccessRequestStatus;
import com.defense.logistics.resource.model.enums.ApplicationRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_account_id", nullable = false) private UserAccount userAccount;
    @Enumerated(EnumType.STRING) @Column(name = "requested_role", nullable = false) private ApplicationRole requestedRole;
    @Column(name = "request_details", length = 500) private String requestDetails;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private AccessRequestStatus status;
    @Column(name = "submitted_at", nullable = false) private LocalDateTime submittedAt;
    @Column(name = "decided_at") private LocalDateTime decidedAt;
    @Column(name = "decided_by") private String decidedBy;
    @Column(name = "rejection_reason", length = 500) private String rejectionReason;
}

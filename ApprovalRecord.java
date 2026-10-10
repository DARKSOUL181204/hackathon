package com.defense.logistics.request.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApprovalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private LogisticsRequest logisticsRequest;

    @Column(name = "approver_id", nullable = false) private String approverId;
    @Column(nullable = false) private String decision; // APPROVED, REJECTED
    private String comments;

    @Column(name = "decided_at", updatable = false) private LocalDateTime decidedAt;
    @PrePersist protected void onCreate() { this.decidedAt = LocalDateTime.now(); }
}

package com.defense.logistics.request.model;
import com.defense.logistics.request.model.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_status_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private LogisticsRequest logisticsRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status") private RequestStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false) private RequestStatus newStatus;

    @Column(name = "changed_by", nullable = false) private String changedBy;
    private String reason;

    @Column(name = "changed_at", updatable = false) private LocalDateTime changedAt;
    @PrePersist protected void onCreate() { this.changedAt = LocalDateTime.now(); }
}

package com.defense.logistics.request.model;
import com.defense.logistics.request.model.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logistics_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LogisticsRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_number", nullable = false, unique = true) private String requestNumber;
    @Column(name = "requesting_unit_id", nullable = false) private Long requestingUnitId; // External Reference
    
    @Column(name = "requested_by", nullable = false) private String requestedBy;
    @Column(nullable = false) private String purpose;
    @Column(nullable = false) private String priority;
    
    @Column(name = "required_by_date") private LocalDateTime requiredByDate;
    @Column(name = "delivery_location") private String deliveryLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private RequestStatus status;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}

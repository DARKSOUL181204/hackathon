package com.defense.logistics.resource.model;
import com.defense.logistics.resource.model.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "maintenance_code", nullable = false, unique = true) private String maintenanceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false) private MaintenanceTargetType targetType;

    // We use a nullable foreign key approach instead of polymorphic mapping to keep it clean in relational DB
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false) private String description;
    @Column(nullable = false) private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "scheduled_date") private LocalDateTime scheduledDate;
    @Column(name = "started_at") private LocalDateTime startedAt;
    @Column(name = "completed_at") private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private MaintenanceStatus status;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}

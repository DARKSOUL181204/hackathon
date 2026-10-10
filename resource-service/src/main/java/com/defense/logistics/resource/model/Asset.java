package com.defense.logistics.resource.model;
import com.defense.logistics.resource.model.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_code", nullable = false, unique = true) private String assetCode;
    @Column(nullable = false) private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private AssetCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false) private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "operational_status", nullable = false) private OperationalStatus operationalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false) private ConditionStatus condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_personnel_id")
    private Personnel assignedPersonnel;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}

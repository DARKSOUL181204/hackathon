package com.defense.logistics.resource.model;
import com.defense.logistics.resource.model.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number", nullable = false, unique = true) private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false) private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false) private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "operational_status", nullable = false) private OperationalStatus operationalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false) private ConditionStatus condition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_personnel_id")
    private Personnel driverPersonnel;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}

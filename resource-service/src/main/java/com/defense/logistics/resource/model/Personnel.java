package com.defense.logistics.resource.model;
import com.defense.logistics.resource.model.enums.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "personnel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Personnel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_number", nullable = false, unique = true) private String serviceNumber;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(nullable = false) private String role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false) private String posting;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false) private AvailabilityStatus availabilityStatus;
    
    @Column(name = "contact_details") private String contactDetails;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}

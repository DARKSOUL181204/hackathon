package com.defense.logistics.request.model;
import com.defense.logistics.request.model.enums.ResourceType;
import com.defense.logistics.request.model.enums.AllocationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_allocations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResourceAllocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_item_id", nullable = false)
    private RequestItem requestItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false) private ResourceType resourceType;

    // We use nullable discrete columns for loose coupling rather than strict polymorphic FKs because these point to other DBs
    @Column(name = "resource_id") private Long resourceId;
    @Column(name = "supply_item_id") private Long supplyItemId;

    @Column(name = "quantity_allocated", nullable = false) private BigDecimal quantityAllocated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private AllocationStatus status;

    @Column(name = "allocated_at", updatable = false) private LocalDateTime allocatedAt;
    @Column(name = "released_at") private LocalDateTime releasedAt;
    @Column(name = "allocated_by") private String allocatedBy;

    @PrePersist protected void onCreate() { this.allocatedAt = LocalDateTime.now(); }
}

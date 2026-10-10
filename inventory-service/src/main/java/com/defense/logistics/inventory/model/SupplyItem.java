package com.defense.logistics.inventory.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supply_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplyItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", nullable = false, unique = true) private String itemCode;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String category;
    @Column(name = "unit_of_measure", nullable = false) private String unitOfMeasure;
    private String description;
    
    @Column(name = "reorder_level", nullable = false) private BigDecimal reorderLevel;
    @Column(nullable = false) private boolean active;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); this.active = true; }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}

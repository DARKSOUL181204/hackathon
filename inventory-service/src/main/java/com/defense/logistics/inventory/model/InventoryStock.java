package com.defense.logistics.inventory.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_stock", uniqueConstraints = {@UniqueConstraint(columnNames = {"supply_item_id", "location"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryStock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_item_id", nullable = false)
    private SupplyItem supplyItem;

    @Column(nullable = false) private String location;
    
    @Column(name = "quantity_on_hand", nullable = false) private BigDecimal quantityOnHand;

    @Version private Integer version; // Optimistic locking

    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PreUpdate @PrePersist protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}

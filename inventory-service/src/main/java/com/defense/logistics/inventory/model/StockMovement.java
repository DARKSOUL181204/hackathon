package com.defense.logistics.inventory.model;
import com.defense.logistics.inventory.model.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_item_id", nullable = false)
    private SupplyItem supplyItem;

    @Column(nullable = false) private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false) private MovementType movementType;

    @Column(nullable = false) private BigDecimal quantity;
    private String reference;
    private String remarks;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}

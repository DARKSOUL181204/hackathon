package com.defense.logistics.inventory.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IssueRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_item_id", nullable = false)
    private SupplyItem supplyItem;

    @Column(nullable = false) private String location;
    @Column(name = "request_id", nullable = false) private Long requestId; // From Request Service
    @Column(name = "quantity_issued", nullable = false) private BigDecimal quantityIssued;
    @Column(name = "issued_to", nullable = false) private String issuedTo;
    
    @Column(name = "issued_at", updatable = false) private LocalDateTime issuedAt;
    private String remarks;

    @PrePersist protected void onCreate() { this.issuedAt = LocalDateTime.now(); }
}

package com.defense.logistics.request.model;
import com.defense.logistics.request.model.enums.ResourceType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "request_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private LogisticsRequest logisticsRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false) private ResourceType resourceType;

    @Column(name = "supply_item_id") private Long supplyItemId;
    @Column(nullable = false) private String description;
    @Column(name = "quantity_requested", nullable = false) private BigDecimal quantityRequested;
    @Column(name = "unit_of_measure") private String unitOfMeasure;
    private String specifications;
}

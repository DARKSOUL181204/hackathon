package com.defense.logistics.inventory.service;
import com.defense.logistics.inventory.model.*;
import com.defense.logistics.inventory.model.enums.MovementType;
import com.defense.logistics.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final SupplyItemRepository itemRepo;
    private final InventoryStockRepository stockRepo;
    private final StockMovementRepository moveRepo;
    private final IssueRecordRepository issueRepo;

    @Transactional
    public void receiveStock(Long itemId, String location, BigDecimal quantity, String reference) {
        if(quantity.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Quantity must be positive");
        SupplyItem item = itemRepo.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        
        InventoryStock stock = stockRepo.findBySupplyItemIdAndLocation(itemId, location)
                .orElse(InventoryStock.builder().supplyItem(item).location(location).quantityOnHand(BigDecimal.ZERO).build());
        
        stock.setQuantityOnHand(stock.getQuantityOnHand().add(quantity));
        stockRepo.save(stock);

        StockMovement movement = StockMovement.builder().supplyItem(item).location(location).movementType(MovementType.RECEIPT).quantity(quantity).reference(reference).build();
        moveRepo.save(movement);
    }

    @Transactional
    public void issueStock(Long itemId, String location, Long requestId, BigDecimal quantity, String issuedTo) {
        if(quantity.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Quantity must be positive");
        SupplyItem item = itemRepo.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if(!item.isActive()) throw new IllegalArgumentException("Item is inactive");

        InventoryStock stock = stockRepo.findBySupplyItemIdAndLocation(itemId, location)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found at location"));
        
        if(stock.getQuantityOnHand().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient stock available");
        }

        stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(quantity));
        stockRepo.save(stock); // Optimistic lock triggers here if concurrent edits occur

        StockMovement movement = StockMovement.builder().supplyItem(item).location(location).movementType(MovementType.ISSUE).quantity(quantity).reference("REQ-"+requestId).build();
        moveRepo.save(movement);

        IssueRecord issue = IssueRecord.builder().supplyItem(item).location(location).requestId(requestId).quantityIssued(quantity).issuedTo(issuedTo).build();
        issueRepo.save(issue);
    }
}

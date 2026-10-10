package com.defense.logistics.inventory.controller;
import com.defense.logistics.inventory.dto.*;
import com.defense.logistics.inventory.service.InventoryService;
import com.defense.logistics.inventory.service.StockQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    private final StockQueryService queryService;

    @PostMapping("/receive")
    public ResponseEntity<String> receive(@Valid @RequestBody StockReceiveRequest req) {
        inventoryService.receiveStock(req.getSupplyItemId(), req.getLocation(), req.getQuantity(), req.getReference());
        return ResponseEntity.ok("Stock received successfully");
    }

    @PostMapping("/issue")
    public ResponseEntity<String> issue(@Valid @RequestBody StockIssueRequest req) {
        inventoryService.issueStock(req.getSupplyItemId(), req.getLocation(), req.getRequestId(), req.getQuantity(), req.getIssuedTo());
        return ResponseEntity.ok("Stock issued successfully");
    }

    @GetMapping("/stock")
    public ResponseEntity<List<StockDto>> getStock() {
        return ResponseEntity.ok(queryService.getAllStock());
    }

    @GetMapping("/{location}/{supplyItemId}")
    public ResponseEntity<StockDto> getStockAtLocation(@PathVariable String location, @PathVariable Long supplyItemId) {
        return ResponseEntity.ok(queryService.getStock(supplyItemId, location));
    }
}

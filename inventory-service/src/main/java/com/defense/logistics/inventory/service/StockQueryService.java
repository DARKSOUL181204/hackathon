package com.defense.logistics.inventory.service;
import com.defense.logistics.inventory.dto.StockDto;
import com.defense.logistics.inventory.repository.InventoryStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockQueryService {
    private final InventoryStockRepository repo;

    public List<StockDto> getAllStock() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
    public StockDto getStock(Long supplyItemId, String location) {
        return toDto(repo.findBySupplyItemIdAndLocation(supplyItemId, location)
                .orElseThrow(() -> new IllegalArgumentException("Stock record not found")));
    }
    private StockDto toDto(com.defense.logistics.inventory.model.InventoryStock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId()); dto.setSupplyItemId(stock.getSupplyItem().getId());
        dto.setItemCode(stock.getSupplyItem().getItemCode()); dto.setLocation(stock.getLocation());
        dto.setQuantityOnHand(stock.getQuantityOnHand());
        return dto;
    }
}

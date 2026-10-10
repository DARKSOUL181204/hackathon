package com.defense.logistics.inventory.repository;
import com.defense.logistics.inventory.model.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {
    Optional<InventoryStock> findBySupplyItemIdAndLocation(Long supplyItemId, String location);
}

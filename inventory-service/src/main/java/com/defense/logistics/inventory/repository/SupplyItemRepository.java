package com.defense.logistics.inventory.repository;
import com.defense.logistics.inventory.model.SupplyItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SupplyItemRepository extends JpaRepository<SupplyItem, Long> {}

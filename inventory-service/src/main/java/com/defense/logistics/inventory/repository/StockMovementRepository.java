package com.defense.logistics.inventory.repository;
import com.defense.logistics.inventory.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {}

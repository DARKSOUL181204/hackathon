package com.defense.logistics.resource.repository;
import com.defense.logistics.resource.model.MaintenanceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MaintenanceOrderRepository extends JpaRepository<MaintenanceOrder, Long> {
    Optional<MaintenanceOrder> findByMaintenanceCode(String maintenanceCode);
}

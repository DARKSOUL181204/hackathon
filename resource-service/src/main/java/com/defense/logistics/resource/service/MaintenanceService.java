package com.defense.logistics.resource.service;
import com.defense.logistics.resource.dto.MaintenanceOrderDto;
import com.defense.logistics.resource.exception.ResourceNotFoundException;
import com.defense.logistics.resource.model.MaintenanceOrder;
import com.defense.logistics.resource.model.Asset;
import com.defense.logistics.resource.model.Vehicle;
import com.defense.logistics.resource.model.enums.MaintenanceTargetType;
import com.defense.logistics.resource.model.enums.OperationalStatus;
import com.defense.logistics.resource.model.enums.MaintenanceStatus;
import com.defense.logistics.resource.repository.MaintenanceOrderRepository;
import com.defense.logistics.resource.repository.AssetRepository;
import com.defense.logistics.resource.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {
    private final MaintenanceOrderRepository repo;
    private final AssetRepository assetRepo;
    private final VehicleRepository vehicleRepo;

    @Transactional
    public MaintenanceOrderDto create(MaintenanceOrderDto dto) {
        Asset asset = null;
        Vehicle vehicle = null;

        if (dto.getTargetType() == MaintenanceTargetType.ASSET) {
            if (dto.getAssetId() == null) throw new IllegalArgumentException("Asset ID is required");
            asset = assetRepo.findById(dto.getAssetId()).orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
            asset.setOperationalStatus(OperationalStatus.UNDER_MAINTENANCE);
            assetRepo.save(asset);
        } else {
            if (dto.getVehicleId() == null) throw new IllegalArgumentException("Vehicle ID is required");
            vehicle = vehicleRepo.findById(dto.getVehicleId()).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
            vehicle.setOperationalStatus(OperationalStatus.UNDER_MAINTENANCE);
            vehicleRepo.save(vehicle);
        }

        MaintenanceOrder mo = MaintenanceOrder.builder()
                .maintenanceCode(dto.getMaintenanceCode())
                .targetType(dto.getTargetType())
                .asset(asset).vehicle(vehicle)
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(dto.getStatus())
                .scheduledDate(dto.getScheduledDate())
                .build();
        
        mo = repo.save(mo);
        dto.setId(mo.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<MaintenanceOrderDto> getAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MaintenanceOrderDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Maintenance order not found")));
    }

    @Transactional
    public MaintenanceOrderDto complete(Long id) {
        MaintenanceOrder order = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Maintenance order not found"));
        if (order.getStatus() == MaintenanceStatus.COMPLETED) throw new IllegalArgumentException("Maintenance order is already completed");
        if (order.getStatus() == MaintenanceStatus.CANCELLED) throw new IllegalArgumentException("Cancelled maintenance cannot be completed");
        order.setStatus(MaintenanceStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        if (order.getAsset() != null) { order.getAsset().setOperationalStatus(OperationalStatus.AVAILABLE); assetRepo.save(order.getAsset()); }
        if (order.getVehicle() != null) { order.getVehicle().setOperationalStatus(OperationalStatus.AVAILABLE); vehicleRepo.save(order.getVehicle()); }
        return toDto(repo.save(order));
    }

    private MaintenanceOrderDto toDto(MaintenanceOrder order) {
        MaintenanceOrderDto dto = new MaintenanceOrderDto();
        dto.setId(order.getId());
        dto.setMaintenanceCode(order.getMaintenanceCode());
        dto.setTargetType(order.getTargetType());
        dto.setAssetId(order.getAsset() == null ? null : order.getAsset().getId());
        dto.setVehicleId(order.getVehicle() == null ? null : order.getVehicle().getId());
        dto.setDescription(order.getDescription());
        dto.setPriority(order.getPriority());
        dto.setStatus(order.getStatus());
        dto.setScheduledDate(order.getScheduledDate());
        dto.setStartedAt(order.getStartedAt());
        dto.setCompletedAt(order.getCompletedAt());
        return dto;
    }
}

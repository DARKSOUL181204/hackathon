package com.defense.logistics.resource.service;
import com.defense.logistics.resource.dto.VehicleDto;
import com.defense.logistics.resource.exception.ResourceNotFoundException;
import com.defense.logistics.resource.model.Vehicle;
import com.defense.logistics.resource.model.Unit;
import com.defense.logistics.resource.model.Personnel;
import com.defense.logistics.resource.repository.VehicleRepository;
import com.defense.logistics.resource.repository.UnitRepository;
import com.defense.logistics.resource.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository repo;
    private final UnitRepository unitRepo;
    private final PersonnelRepository personnelRepo;

    @Transactional
    public VehicleDto create(VehicleDto dto) {
        if(repo.findByRegistrationNumber(dto.getRegistrationNumber()).isPresent()) throw new IllegalArgumentException("Registration number exists");
        Unit unit = unitRepo.findById(dto.getUnitId()).orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        Personnel driver = null;
        if(dto.getDriverPersonnelId() != null) {
            driver = personnelRepo.findById(dto.getDriverPersonnelId()).orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        }
        Vehicle v = Vehicle.builder().registrationNumber(dto.getRegistrationNumber()).vehicleType(dto.getVehicleType()).unit(unit).location(dto.getLocation()).operationalStatus(dto.getOperationalStatus()).condition(dto.getCondition()).driverPersonnel(driver).build();
        v = repo.save(v);
        dto.setId(v.getId());
        return dto;
    }
    public List<VehicleDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
    public VehicleDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found")));
    }
    private VehicleDto toDto(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId()); dto.setRegistrationNumber(vehicle.getRegistrationNumber());
        dto.setVehicleType(vehicle.getVehicleType()); dto.setUnitId(vehicle.getUnit().getId());
        dto.setLocation(vehicle.getLocation()); dto.setCondition(vehicle.getCondition());
        dto.setOperationalStatus(vehicle.getOperationalStatus());
        dto.setDriverPersonnelId(vehicle.getDriverPersonnel() == null ? null : vehicle.getDriverPersonnel().getId());
        return dto;
    }
}

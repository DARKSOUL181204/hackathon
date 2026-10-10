package com.defense.logistics.resource.service;
import com.defense.logistics.resource.dto.AssetDto;
import com.defense.logistics.resource.exception.ResourceNotFoundException;
import com.defense.logistics.resource.model.Asset;
import com.defense.logistics.resource.model.Unit;
import com.defense.logistics.resource.model.Personnel;
import com.defense.logistics.resource.repository.AssetRepository;
import com.defense.logistics.resource.repository.UnitRepository;
import com.defense.logistics.resource.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetService {
    private final AssetRepository repo;
    private final UnitRepository unitRepo;
    private final PersonnelRepository personnelRepo;

    @Transactional
    public AssetDto create(AssetDto dto) {
        if(repo.findByAssetCode(dto.getAssetCode()).isPresent()) throw new IllegalArgumentException("Asset code exists");
        Unit unit = unitRepo.findById(dto.getUnitId()).orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        Personnel assignee = dto.getAssignedPersonnelId() == null ? null : personnelRepo.findById(dto.getAssignedPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned personnel not found"));
        Asset a = Asset.builder().assetCode(dto.getAssetCode()).name(dto.getName()).category(dto.getCategory()).unit(unit)
                .location(dto.getLocation()).operationalStatus(dto.getOperationalStatus()).condition(dto.getCondition())
                .assignedPersonnel(assignee).build();
        a = repo.save(a);
        dto.setId(a.getId());
        return dto;
    }
    public List<AssetDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
    public AssetDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset not found")));
    }
    private AssetDto toDto(Asset asset) {
        AssetDto dto = new AssetDto();
        dto.setId(asset.getId()); dto.setAssetCode(asset.getAssetCode()); dto.setName(asset.getName());
        dto.setUnitId(asset.getUnit().getId()); dto.setCategory(asset.getCategory()); dto.setLocation(asset.getLocation());
        dto.setCondition(asset.getCondition()); dto.setOperationalStatus(asset.getOperationalStatus());
        dto.setAssignedPersonnelId(asset.getAssignedPersonnel() == null ? null : asset.getAssignedPersonnel().getId());
        return dto;
    }
}

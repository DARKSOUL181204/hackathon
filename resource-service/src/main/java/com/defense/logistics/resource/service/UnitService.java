package com.defense.logistics.resource.service;
import com.defense.logistics.resource.dto.UnitDto;
import com.defense.logistics.resource.exception.ResourceNotFoundException;
import com.defense.logistics.resource.model.Unit;
import com.defense.logistics.resource.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;

    @Transactional
    public UnitDto createUnit(UnitDto dto) {
        if(unitRepository.findByCode(dto.getCode()).isPresent()) throw new IllegalArgumentException("Unit code already exists");
        Unit unit = Unit.builder().name(dto.getName()).code(dto.getCode()).location(dto.getLocation()).status(dto.getStatus()).build();
        unit = unitRepository.save(unit);
        dto.setId(unit.getId());
        return dto;
    }

    public UnitDto getUnit(Long id) {
        Unit u = unitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        UnitDto dto = new UnitDto(); dto.setId(u.getId()); dto.setName(u.getName()); dto.setCode(u.getCode()); dto.setLocation(u.getLocation()); dto.setStatus(u.getStatus());
        return dto;
    }

    public List<UnitDto> getAllUnits() {
        return unitRepository.findAll().stream().map(u -> {
            UnitDto dto = new UnitDto(); dto.setId(u.getId()); dto.setName(u.getName()); dto.setCode(u.getCode()); dto.setLocation(u.getLocation()); dto.setStatus(u.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }
}

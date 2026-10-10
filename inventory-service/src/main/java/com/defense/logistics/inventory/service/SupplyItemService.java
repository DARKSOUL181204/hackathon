package com.defense.logistics.inventory.service;
import com.defense.logistics.inventory.dto.SupplyItemDto;
import com.defense.logistics.inventory.model.SupplyItem;
import com.defense.logistics.inventory.repository.SupplyItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyItemService {
    private final SupplyItemRepository repo;

    @Transactional
    public SupplyItemDto create(SupplyItemDto dto) {
        SupplyItem item = SupplyItem.builder()
                .itemCode(dto.getItemCode()).name(dto.getName())
                .category(dto.getCategory()).unitOfMeasure(dto.getUnitOfMeasure())
                .description(dto.getDescription()).reorderLevel(dto.getReorderLevel())
                .build();
        item = repo.save(item);
        dto.setId(item.getId());
        dto.setActive(item.isActive());
        return dto;
    }

    public List<SupplyItemDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
    public SupplyItemDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Supply item not found")));
    }
    private SupplyItemDto toDto(SupplyItem item) {
        SupplyItemDto dto = new SupplyItemDto();
        dto.setId(item.getId()); dto.setItemCode(item.getItemCode()); dto.setName(item.getName());
        dto.setCategory(item.getCategory()); dto.setUnitOfMeasure(item.getUnitOfMeasure());
        dto.setDescription(item.getDescription()); dto.setReorderLevel(item.getReorderLevel()); dto.setActive(item.isActive());
        return dto;
    }
}

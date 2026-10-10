package com.defense.logistics.inventory.controller;
import com.defense.logistics.inventory.dto.SupplyItemDto;
import com.defense.logistics.inventory.service.SupplyItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/supplies")
@RequiredArgsConstructor
public class SupplyItemController {
    private final SupplyItemService service;

    @PostMapping
    public ResponseEntity<SupplyItemDto> create(@Valid @RequestBody SupplyItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<SupplyItemDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SupplyItemDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

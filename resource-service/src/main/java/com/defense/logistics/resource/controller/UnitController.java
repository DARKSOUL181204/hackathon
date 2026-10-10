package com.defense.logistics.resource.controller;
import com.defense.logistics.resource.dto.UnitDto;
import com.defense.logistics.resource.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
public class UnitController {
    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<UnitDto> create(@Valid @RequestBody UnitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unitService.createUnit(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getUnit(id));
    }

    @GetMapping
    public ResponseEntity<List<UnitDto>> getAll() {
        return ResponseEntity.ok(unitService.getAllUnits());
    }
}

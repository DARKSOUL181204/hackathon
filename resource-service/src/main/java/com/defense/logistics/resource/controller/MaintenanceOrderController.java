package com.defense.logistics.resource.controller;
import com.defense.logistics.resource.dto.MaintenanceOrderDto;
import com.defense.logistics.resource.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance-orders")
@RequiredArgsConstructor
public class MaintenanceOrderController {
    private final MaintenanceService service;
    @PostMapping
    public ResponseEntity<MaintenanceOrderDto> create(@Valid @RequestBody MaintenanceOrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceOrderDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceOrderDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PostMapping("/{id}/complete")
    public ResponseEntity<MaintenanceOrderDto> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.complete(id));
    }
}

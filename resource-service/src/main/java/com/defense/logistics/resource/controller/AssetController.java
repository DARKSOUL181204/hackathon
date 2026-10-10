package com.defense.logistics.resource.controller;
import com.defense.logistics.resource.dto.AssetDto;
import com.defense.logistics.resource.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService service;
    @PostMapping
    public ResponseEntity<AssetDto> create(@Valid @RequestBody AssetDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<AssetDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @GetMapping
    public ResponseEntity<List<AssetDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

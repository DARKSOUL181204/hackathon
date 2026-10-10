package com.defense.logistics.resource.controller;
import com.defense.logistics.resource.dto.PersonnelDto;
import com.defense.logistics.resource.service.PersonnelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.defense.logistics.resource.model.enums.AvailabilityStatus;

@RestController
@RequestMapping("/api/v1/personnels")
@RequiredArgsConstructor
public class PersonnelController {
    private final PersonnelService service;
    @PostMapping
    public ResponseEntity<PersonnelDto> create(@Valid @RequestBody PersonnelDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<PersonnelDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @GetMapping
    public ResponseEntity<List<PersonnelDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    @PatchMapping("/{id}/availability")
    public ResponseEntity<PersonnelDto> updateAvailability(@PathVariable Long id, @RequestParam AvailabilityStatus status) {
        return ResponseEntity.ok(service.updateAvailability(id, status));
    }
}

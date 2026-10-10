package com.defense.logistics.request.controller;
import com.defense.logistics.request.dto.*;
import com.defense.logistics.request.service.RequestOrchestratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class LogisticsRequestController {
    private final RequestOrchestratorService orchestrator;

    @PostMapping
    public ResponseEntity<LogisticsRequestDto> create(@Valid @RequestBody LogisticsRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orchestrator.createRequest(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogisticsRequestDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(orchestrator.getById(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable Long id, @Valid @RequestBody ApprovalDto dto) {
        dto.setDecision("APPROVED");
        orchestrator.processApproval(id, dto);
        return ResponseEntity.ok("Request approval processed");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable Long id, @Valid @RequestBody ApprovalDto dto) {
        dto.setDecision("REJECTED");
        orchestrator.processApproval(id, dto);
        return ResponseEntity.ok("Request rejected");
    }

    @PostMapping("/{id}/allocate")
    public ResponseEntity<String> allocate(@PathVariable Long id, @Valid @RequestBody AllocationDto dto) {
        orchestrator.allocateResource(id, dto);
        return ResponseEntity.ok("Resource allocated successfully");
    }
    @GetMapping
    public ResponseEntity<List<LogisticsRequestDto>> getAll() {
        return ResponseEntity.ok(orchestrator.getAll());
    }
}

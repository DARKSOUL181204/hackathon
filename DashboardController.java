package com.defense.logistics.request.controller;
import com.defense.logistics.request.repository.LogisticsRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final LogisticsRequestRepository requestRepo;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        long totalRequests = requestRepo.count();
        // Here we would aggregate counts by status and make downstream calls to Resource & Inventory for readiness metrics
        // Returning simple map for Phase 6 completion
        return ResponseEntity.ok(Map.of(
            "totalRequests", totalRequests,
            "status", "Healthy"
        ));
    }
}

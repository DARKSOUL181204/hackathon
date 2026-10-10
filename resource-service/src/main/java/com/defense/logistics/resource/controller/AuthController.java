package com.defense.logistics.resource.controller;

import com.defense.logistics.resource.dto.AccessRequestSubmission;
import com.defense.logistics.resource.dto.AccessRequestView;
import com.defense.logistics.resource.dto.AuthResponse;
import com.defense.logistics.resource.dto.LoginRequest;
import com.defense.logistics.resource.model.enums.AccessRequestStatus;
import com.defense.logistics.resource.service.AccessManagementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AccessManagementService access;

    @PostMapping("/auth/access-requests")
    public ResponseEntity<AccessRequestView> requestAccess(@Valid @RequestBody AccessRequestSubmission submission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(access.submit(submission));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) { return ResponseEntity.ok(access.login(request)); }

    @GetMapping("/auth/access-requests/status/{username}")
    public ResponseEntity<List<AccessRequestView>> requestStatus(@PathVariable String username) { return ResponseEntity.ok(access.statusFor(username)); }

    @GetMapping("/admin/access-requests")
    public ResponseEntity<List<AccessRequestView>> list(@RequestParam(required = false) AccessRequestStatus status) { return ResponseEntity.ok(access.list(status)); }

    @PostMapping("/admin/access-requests/{id}/approve")
    public ResponseEntity<AccessRequestView> approve(@PathVariable Long id, Authentication authentication) { return ResponseEntity.ok(access.approve(id, authentication.getName())); }

    @PostMapping("/admin/access-requests/{id}/reject")
    public ResponseEntity<AccessRequestView> reject(@PathVariable Long id, @RequestParam(required = false) String reason, Authentication authentication) { return ResponseEntity.ok(access.reject(id, authentication.getName(), reason)); }
    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) { return ResponseEntity.ok(access.profile(authentication.getName())); }
}

package com.defense.logistics.resource.service;

import com.defense.logistics.resource.dto.*;
import com.defense.logistics.resource.exception.ResourceNotFoundException;
import com.defense.logistics.resource.model.AccessRequest;
import com.defense.logistics.resource.model.UserAccount;
import com.defense.logistics.resource.model.enums.*;
import com.defense.logistics.resource.repository.AccessRequestRepository;
import com.defense.logistics.resource.repository.UserAccountRepository;
import com.defense.logistics.resource.repository.PersonnelRepository;
import com.defense.logistics.resource.security.JwtTokenService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccessManagementService {
    private final UserAccountRepository accounts;
    private final AccessRequestRepository requests;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokens;
    private final PersonnelRepository personnel;
    @Value("${app.bootstrap.admin.username:admin}") private String bootstrapUsername;
    @Value("${app.bootstrap.admin.password:ChangeMe!2026}") private String bootstrapPassword;

    @Transactional
    public void ensureBootstrapAdmin() {
        if (accounts.findByUsernameIgnoreCase(bootstrapUsername).isEmpty()) {
            accounts.save(UserAccount.builder().username(bootstrapUsername).fullName("System Administrator")
                    .passwordHash(passwordEncoder.encode(bootstrapPassword)).role(ApplicationRole.ADMIN)
                    .accountStatus(AccountStatus.ACTIVE).build());
        }
    }

    @Transactional
    public AccessRequestView submit(AccessRequestSubmission input) {
        String username = input.getUsername().trim().toLowerCase();
        UserAccount account = accounts.findByUsernameIgnoreCase(username).orElse(null);
        if (account != null && account.getAccountStatus() == AccountStatus.ACTIVE) throw new IllegalArgumentException("This account already has active access. Please sign in.");
        if (account != null && requests.existsByUserAccountAndStatus(account, AccessRequestStatus.PENDING)) throw new IllegalArgumentException("An access request for this username is already pending review.");
        if (account == null) {
            account = UserAccount.builder().username(username).fullName(input.getFullName().trim())
                    .passwordHash(passwordEncoder.encode(input.getPassword())).accountStatus(AccountStatus.PENDING).build();
        } else {
            account.setFullName(input.getFullName().trim());
            account.setPasswordHash(passwordEncoder.encode(input.getPassword()));
            account.setRole(null);
            account.setAccountStatus(AccountStatus.PENDING);
        }
        account = accounts.save(account);
        AccessRequest request = new AccessRequest();
        request.setUserAccount(account);
        request.setRequestedRole(input.getRequestedRole());
        request.setRequestDetails(blankToNull(input.getRequestDetails()));
        request.setStatus(AccessRequestStatus.PENDING);
        request.setSubmittedAt(LocalDateTime.now());
        return view(requests.save(request));
    }

    public AuthResponse login(LoginRequest input) {
        UserAccount account = accounts.findByUsernameIgnoreCase(input.getUsername().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        if (!passwordEncoder.matches(input.getPassword(), account.getPasswordHash())) throw new IllegalArgumentException("Invalid username or password.");
        if (account.getAccountStatus() == AccountStatus.PENDING) throw new IllegalArgumentException("Your access request is pending administrator review.");
        if (account.getAccountStatus() != AccountStatus.ACTIVE || account.getRole() == null) throw new IllegalArgumentException("Your access request was not approved. Contact an administrator if you need help.");
        return new AuthResponse(tokens.create(account.getUsername(), account.getRole().name()), account.getUsername(), account.getFullName(), account.getRole().name());
    }

    public List<AccessRequestView> list(AccessRequestStatus status) {
        List<AccessRequest> result = status == null ? requests.findAllByOrderBySubmittedAtDesc() : requests.findAllByStatusOrderBySubmittedAtDesc(status);
        return result.stream().map(this::view).toList();
    }

    public List<AccessRequestView> statusFor(String username) {
        return accounts.findByUsernameIgnoreCase(username.trim()).map(account -> requests.findAllByUserAccountOrderBySubmittedAtDesc(account).stream().map(this::view).toList()).orElse(List.of());
    }
    @Transactional(readOnly = true)
    public Map<String, Object> profile(String username) {
        UserAccount account = accounts.findByUsernameIgnoreCase(username).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("username", account.getUsername());
        result.put("fullName", account.getFullName());
        result.put("role", account.getRole() == null ? null : account.getRole().name());
        result.put("accountStatus", account.getAccountStatus().name());
        personnel.findByServiceNumberIgnoreCase(account.getUsername()).ifPresent(person -> {
            result.put("personnelLinked", true);
            result.put("serviceNumber", person.getServiceNumber());
            result.put("personnelRole", person.getRole());
            result.put("availabilityStatus", person.getAvailabilityStatus().name());
            result.put("unitName", person.getUnit().getName());
            result.put("unitCode", person.getUnit().getCode());
            result.put("unitLocation", person.getUnit().getLocation());
            result.put("posting", person.getPosting());
        });
        result.putIfAbsent("personnelLinked", false);
        return result;
    }

    @Transactional
    public AccessRequestView approve(Long id, String administrator) {
        AccessRequest request = pending(id);
        request.setStatus(AccessRequestStatus.APPROVED);
        request.setDecidedAt(LocalDateTime.now());
        request.setDecidedBy(administrator);
        UserAccount account = request.getUserAccount();
        account.setRole(request.getRequestedRole());
        account.setAccountStatus(AccountStatus.ACTIVE);
        accounts.save(account);
        return view(requests.save(request));
    }

    @Transactional
    public AccessRequestView reject(Long id, String administrator, String reason) {
        AccessRequest request = pending(id);
        request.setStatus(AccessRequestStatus.REJECTED);
        request.setDecidedAt(LocalDateTime.now());
        request.setDecidedBy(administrator);
        request.setRejectionReason(blankToNull(reason));
        UserAccount account = request.getUserAccount();
        account.setRole(null);
        account.setAccountStatus(AccountStatus.REJECTED);
        accounts.save(account);
        return view(requests.save(request));
    }

    private AccessRequest pending(Long id) {
        AccessRequest request = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Access request not found"));
        if (request.getStatus() != AccessRequestStatus.PENDING) throw new IllegalArgumentException("This access request has already been decided.");
        return request;
    }
    private AccessRequestView view(AccessRequest r) { return AccessRequestView.builder().id(r.getId()).fullName(r.getUserAccount().getFullName()).username(r.getUserAccount().getUsername()).requestedRole(r.getRequestedRole().name()).requestDetails(r.getRequestDetails()).status(r.getStatus().name()).submittedAt(r.getSubmittedAt()).decidedAt(r.getDecidedAt()).decidedBy(r.getDecidedBy()).rejectionReason(r.getRejectionReason()).build(); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}

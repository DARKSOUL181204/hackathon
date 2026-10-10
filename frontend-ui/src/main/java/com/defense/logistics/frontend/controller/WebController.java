package com.defense.logistics.frontend.controller;
import com.defense.logistics.frontend.service.BackendClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"username", "role", "accessToken", "fullName"})
public class WebController {
    private final BackendClientService backendService;

    // --- GET ROUTES ---
    @GetMapping("/") public String dashboard(Model model) { 
        if (!model.containsAttribute("role")) return "redirect:/login";
        model.addAttribute("summary", backendService.getDashboardSummary()); 
        model.addAttribute("units", backendService.getUnits()); 
        return "dashboard"; 
    }
    @GetMapping("/personnel") public String personnel(Model model) { 
        if (!model.containsAttribute("role")) return "redirect:/login";
        model.addAttribute("personnelList", backendService.getPersonnel()); return "personnel"; 
    }
    @GetMapping("/personnel/{id}") public String personnelProfile(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("role") || !"ADMIN".equals(model.getAttribute("role"))) return "redirect:/login";
        Map<String, Object> person = backendService.getPersonnel(id);
        if (person.isEmpty()) return "redirect:/personnel";
        model.addAttribute("person", person);
        model.addAttribute("unit", backendService.getUnit(Long.valueOf(String.valueOf(person.get("unitId")))));
        model.addAttribute("assignedAssets", matchingPersonnel(backendService.getAssets(), "assignedPersonnelId", id));
        model.addAttribute("assignedVehicles", matchingPersonnel(backendService.getVehicles(), "driverPersonnelId", id));
        return "personnel-profile";
    }
    @GetMapping("/assets") public String assets(Model model) { 
        if (!model.containsAttribute("role")) return "redirect:/login";
        model.addAttribute("assetList", backendService.getAssets()); return "assets"; 
    }
    @GetMapping("/fleet") public String fleet(Model model) { 
        if (!model.containsAttribute("role")) return "redirect:/login";
        model.addAttribute("vehicleList", backendService.getVehicles()); return "fleet"; 
    }
    @GetMapping("/inventory") public String inventory(Model model) { 
        if (!model.containsAttribute("role")) return "redirect:/login";
        model.addAttribute("supplyList", backendService.getSupplies()); model.addAttribute("stockList", backendService.getStock()); return "inventory"; 
    }
    @GetMapping("/requests") public String requests(@RequestParam(required = false) String query,
                                                     @RequestParam(required = false) String status,
                                                     @SessionAttribute(value = "role", required = false) String role,
                                                     @SessionAttribute(value = "username", required = false) String username,
                                                     Model model) {
        if (role == null || username == null) return "redirect:/login";
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        String normalizedStatus = isRequestStatus(status) ? status : "";
        List<Map<String, Object>> requests = backendService.getRequests().stream()
                .filter(request -> "ADMIN".equals(role) || username.equals(String.valueOf(request.get("requestedBy"))))
                .filter(request -> normalizedStatus.isEmpty() || normalizedStatus.equals(String.valueOf(request.get("status"))))
                .filter(request -> normalizedQuery.isEmpty() || request.values().stream()
                        .anyMatch(value -> String.valueOf(value).toLowerCase(Locale.ROOT).contains(normalizedQuery)))
                .toList();
        model.addAttribute("requestList", requests);
        model.addAttribute("requestQuery", query == null ? "" : query.trim());
        model.addAttribute("requestStatus", normalizedStatus);
        model.addAttribute("supplyList", backendService.getSupplies());
        model.addAttribute("units", backendService.getUnits());
        return "requests"; 
    }
    @GetMapping("/maintenance") public String maintenance(Model model) { 
        if (!model.containsAttribute("role")) return "redirect:/login";
        model.addAttribute("maintenanceList", backendService.getMaintenanceOrders()); return "maintenance"; 
    }
    @GetMapping("/my-status") public String myStatus(Model model) {
        if (!model.containsAttribute("role")) return "redirect:/login";
        model.addAttribute("profile", backendService.getMyAccessProfile());
        return "my-status";
    }

    
    @GetMapping("/admin") public String adminOverview(Model model) {
        if (!model.containsAttribute("role") || !"ADMIN".equals(model.getAttribute("role"))) return "redirect:/login";
        model.addAttribute("units", backendService.getUnits().size());
        model.addAttribute("personnel", backendService.getPersonnel().size());
        model.addAttribute("vehicles", backendService.getVehicles().size());
        model.addAttribute("requests", backendService.getRequests().size());
        model.addAttribute("supplies", backendService.getSupplies().size());
        model.addAttribute("stock", backendService.getStock().size());
        model.addAttribute("accessRequests", backendService.getAccessRequests());
        model.addAttribute("pendingAccessRequests", backendService.getPendingAccessRequestCount());
        return "admin";
    }

    // --- AUTH ROUTES ---
    @GetMapping("/login") public String login() { return "login"; }
    @PostMapping("/login") public String processLogin(@RequestParam String username, @RequestParam String password, Model model, RedirectAttributes flash) {
        BackendClientService.LoginResult result = backendService.login(username, password);
        if (!result.successful()) {
            flash.addFlashAttribute("errorMessage", result.message());
            return "redirect:/login";
        }
        model.addAttribute("username", result.username());
        model.addAttribute("fullName", result.fullName());
        model.addAttribute("role", result.role());
        model.addAttribute("accessToken", result.token());
        return "redirect:/";
    }
    @GetMapping("/signup") public String signup() { return "signup"; }
    @PostMapping("/signup") public String processSignup(@RequestParam Map<String, String> formData, RedirectAttributes flash) {
        BackendClientService.OperationResult result = backendService.submitAccessRequest(formData);
        flash.addFlashAttribute(result.successful() ? "successMessage" : "errorMessage", result.message());
        return result.successful() ? "redirect:/access-status?username=" + UriUtils.encode(formData.get("username"), StandardCharsets.UTF_8) : "redirect:/signup";
    }
    @GetMapping("/access-status") public String accessStatus(@RequestParam(required = false) String username, Model model) {
        model.addAttribute("trackedUsername", username);
        model.addAttribute("accessRequestStatus", username == null || username.isBlank() ? List.of() : backendService.getAccessRequestStatus(username));
        return "access-status";
    }
    
    @GetMapping("/logout") public String logout(SessionStatus status) {
        status.setComplete();
        return "redirect:/login";
    }

    // --- POST ROUTES ---
    @PostMapping("/units/create") public String createUnit(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.createUnit(formData), "/", flash); }
    @PostMapping("/personnel/create") public String createPersonnel(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.createPersonnel(formData), "/personnel", flash); }
    @PostMapping("/assets/create") public String createAsset(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.createAsset(formData), "/assets", flash); }
    @PostMapping("/vehicles/create") public String createVehicle(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.createVehicle(formData), "/fleet", flash); }
    @PostMapping("/maintenance/create") public String createMaintenance(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.createMaintenanceOrder(formData), "/maintenance", flash); }
    @PostMapping("/maintenance/{id}/complete") public String completeMaintenance(@PathVariable Long id, RedirectAttributes flash) { return after(backendService.completeMaintenanceOrder(id), "/maintenance", flash); }
    @PostMapping("/personnel/{id}/availability") public String updatePersonnelAvailability(@PathVariable Long id, @RequestParam String status, RedirectAttributes flash) { return after(backendService.updatePersonnelAvailability(id, status), "/personnel", flash); }
    @PostMapping("/supplies/create") public String createSupply(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.createSupply(formData), "/inventory", flash); }
    @PostMapping("/inventory/receive") public String receiveStock(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.receiveStock(formData), "/inventory", flash); }

    @PostMapping("/requests/create") public String createRequest(@RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.createRequest(formData), "/requests", flash); }
    @PostMapping("/requests/approve/{id}") public String approveRequest(@PathVariable Long id, @SessionAttribute String username, RedirectAttributes flash) { return after(backendService.approveRequest(id, username), "/requests", flash); }
    @PostMapping("/requests/reject/{id}") public String rejectRequest(@PathVariable Long id, @SessionAttribute String username, RedirectAttributes flash) { return after(backendService.rejectRequest(id, username), "/requests", flash); }
    @PostMapping("/requests/allocate/{id}") public String allocateRequest(@PathVariable Long id, @RequestParam Map<String, String> formData, RedirectAttributes flash) { return after(backendService.allocateRequest(id, formData), "/requests", flash); }
    @PostMapping("/access-requests/{id}/approve") public String approveAccessRequest(@PathVariable Long id, @SessionAttribute("role") String role, RedirectAttributes flash) {
        if (!"ADMIN".equals(role)) return forbidden("/", flash);
        return after(backendService.approveAccessRequest(id), "/admin", flash);
    }
    @PostMapping("/access-requests/{id}/reject") public String rejectAccessRequest(@PathVariable Long id, @RequestParam(required = false) String reason, @SessionAttribute("role") String role, RedirectAttributes flash) {
        if (!"ADMIN".equals(role)) return forbidden("/", flash);
        return after(backendService.rejectAccessRequest(id, reason), "/admin", flash);
    }

    private String after(BackendClientService.OperationResult result, String path, RedirectAttributes flash) {
        flash.addFlashAttribute(result.successful() ? "successMessage" : "errorMessage", result.message());
        return "redirect:" + path;
    }
    private String forbidden(String path, RedirectAttributes flash) {
        flash.addFlashAttribute("errorMessage", "Administrator access is required for this action.");
        return "redirect:" + path;
    }

    private List<Map<String, Object>> matchingPersonnel(List<Map<String, Object>> records, String key, Long personnelId) {
        return records.stream().filter(record -> personnelId.toString().equals(String.valueOf(record.get(key)))).toList();
    }
    private boolean isRequestStatus(String status) {
        return "CREATED".equals(status) || "APPROVED".equals(status) || "REJECTED".equals(status) || "ALLOCATED".equals(status);
    }
}

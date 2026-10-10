package com.defense.logistics.frontend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackendClientService {
    private final RestClient restClient;

    @Value("${backend.resource.url}") private String resourceUrl;
    @Value("${backend.inventory.url}") private String inventoryUrl;
    @Value("${backend.request.url}") private String requestUrl;

    public BackendClientService() {
        this.restClient = RestClient.builder().build();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getList(String uri) {
        try {
            List<Map<String, Object>> response = authenticatedGet(uri).retrieve().body(List.class);
            return response == null ? List.of() : response;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getObject(String uri) {
        try {
            Map<String, Object> response = authenticatedGet(uri).retrieve().body(Map.class);
            return response == null ? Map.of() : response;
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    public List<Map<String, Object>> getUnits() { return getList(resourceUrl + "/units"); }
    public List<Map<String, Object>> getPersonnel() { return getList(resourceUrl + "/personnels"); }
    public List<Map<String, Object>> getAssets() { return getList(resourceUrl + "/assets"); }
    public List<Map<String, Object>> getVehicles() { return getList(resourceUrl + "/vehicles"); }
    public List<Map<String, Object>> getMaintenanceOrders() { return getList(resourceUrl + "/maintenance-orders"); }
    public List<Map<String, Object>> getSupplies() { return getList(inventoryUrl + "/supplies"); }
    public List<Map<String, Object>> getStock() { return getList(inventoryUrl + "/inventory/stock"); }
    public List<Map<String, Object>> getRequests() { return getList(requestUrl + "/requests"); }
    public Map<String, Object> getPersonnel(Long id) { return getObject(resourceUrl + "/personnels/" + id); }
    public Map<String, Object> getUnit(Long id) { return getObject(resourceUrl + "/units/" + id); }
    public Map<String, Object> getMyAccessProfile() { return getObject(resourceUrl + "/auth/me"); }

    public Map<String, Object> getDashboardSummary() {
        int pendingRequests = (int) getRequests().stream()
                .filter(request -> "CREATED".equals(request.get("status")))
                .count();
        return Map.of(
                "totalUnits", getUnits().size(),
                "totalPersonnel", getPersonnel().size(),
                "pendingRequests", pendingRequests,
                "totalVehicles", getVehicles().size());
    }

    public LoginResult login(String username, String password) {
        try {
            @SuppressWarnings("unchecked") Map<String, Object> response = restClient.post().uri(resourceUrl + "/auth/login")
                    .body(Map.of("username", username, "password", password)).retrieve().body(Map.class);
            if (response == null || response.get("token") == null) return LoginResult.failure("Authentication service did not return a session.");
            return LoginResult.success(String.valueOf(response.get("token")), String.valueOf(response.get("username")),
                    String.valueOf(response.get("fullName")), String.valueOf(response.get("role")));
        } catch (RestClientResponseException exception) { return LoginResult.failure(errorMessage(exception)); }
        catch (Exception exception) { return LoginResult.failure("Unable to contact the authentication service."); }
    }

    public OperationResult submitAccessRequest(Map<String, String> form) {
        return postPublic(resourceUrl + "/auth/access-requests", Map.of(
                "fullName", form.get("fullname"), "username", form.get("username"), "password", form.get("password"),
                "requestedRole", "EMPLOYEE", "requestDetails", optional(form.get("requestDetails"))),
                "Your access request is pending administrator review.");
    }

    public List<Map<String, Object>> getAccessRequests() { return getList(resourceUrl + "/admin/access-requests"); }
    public long getPendingAccessRequestCount() { return getAccessRequests().stream().filter(request -> "PENDING".equals(request.get("status"))).count(); }
    public List<Map<String, Object>> getAccessRequestStatus(String username) {
        try { return restClient.get().uri(resourceUrl + "/auth/access-requests/status/{username}", username).retrieve().body(List.class); }
        catch (Exception ignored) { return List.of(); }
    }
    public OperationResult approveAccessRequest(Long id) { return post(resourceUrl + "/admin/access-requests/" + id + "/approve", Map.of(), "Access request approved. The user can now sign in."); }
    public OperationResult rejectAccessRequest(Long id, String reason) {
        String uri = resourceUrl + "/admin/access-requests/" + id + "/reject" + (reason == null || reason.isBlank() ? "" : "?reason=" + java.net.URLEncoder.encode(reason, java.nio.charset.StandardCharsets.UTF_8));
        return post(uri, Map.of(), "Access request rejected.");
    }

    public OperationResult createUnit(Map<String, String> form) {
        return post(resourceUrl + "/units", Map.of(
                "name", form.get("name"), "code", form.get("code"),
                "location", form.get("location"), "status", form.get("status")), "Unit registered.");
    }

    public OperationResult createPersonnel(Map<String, String> form) {
        return post(resourceUrl + "/personnels", Map.of(
                "serviceNumber", form.get("serviceNumber"), "fullName", form.get("fullName"),
                "role", form.get("role"), "unitId", longValue(form, "unitId"),
                "posting", form.get("posting"), "availabilityStatus", form.get("availabilityStatus")), "Personnel registered.");
    }

    public OperationResult createAsset(Map<String, String> form) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("assetCode", form.get("assetCode"));
        payload.put("name", form.get("name"));
        payload.put("unitId", longValue(form, "unitId"));
        payload.put("category", form.get("category"));
        payload.put("location", form.get("location"));
        payload.put("condition", form.get("condition"));
        payload.put("operationalStatus", form.get("operationalStatus"));
        addOptionalLong(payload, "assignedPersonnelId", form.get("assignedPersonnelId"));
        return post(resourceUrl + "/assets", payload, "Asset registered.");
    }

    public OperationResult createVehicle(Map<String, String> form) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("registrationNumber", form.get("registrationNumber"));
        payload.put("unitId", longValue(form, "unitId"));
        payload.put("vehicleType", form.get("vehicleType"));
        payload.put("location", form.get("location"));
        payload.put("condition", form.get("condition"));
        payload.put("operationalStatus", form.get("operationalStatus"));
        addOptionalLong(payload, "driverPersonnelId", form.get("driverPersonnelId"));
        return post(resourceUrl + "/vehicles", payload, "Vehicle registered.");
    }

    public OperationResult createMaintenanceOrder(Map<String, String> form) {
        Map<String, Object> payload = new HashMap<>();
        String targetType = form.get("targetType");
        payload.put("maintenanceCode", form.get("maintenanceCode"));
        payload.put("targetType", targetType);
        payload.put("description", form.get("description"));
        payload.put("priority", form.get("priority"));
        payload.put("status", "SCHEDULED");
        payload.put("scheduledDate", form.get("scheduledDate"));
        payload.put("ASSET".equals(targetType) ? "assetId" : "vehicleId", longValue(form, "targetId"));
        return post(resourceUrl + "/maintenance-orders", payload, "Maintenance order scheduled.");
    }
    public OperationResult completeMaintenanceOrder(Long id) {
        return post(resourceUrl + "/maintenance-orders/" + id + "/complete", Map.of(), "Maintenance completed. The target is available again.");
    }
    public OperationResult updatePersonnelAvailability(Long id, String status) {
        try {
            authenticatedPatch(resourceUrl + "/personnels/" + id + "/availability?status=" + java.net.URLEncoder.encode(status, java.nio.charset.StandardCharsets.UTF_8)).retrieve().toBodilessEntity();
            return OperationResult.success("Personnel availability updated.");
        } catch (RestClientResponseException exception) { return OperationResult.failure(errorMessage(exception)); }
        catch (Exception exception) { return OperationResult.failure("Unable to update personnel availability: " + exception.getMessage()); }
    }

    public OperationResult createSupply(Map<String, String> form) {
        return post(inventoryUrl + "/supplies", Map.of(
                "itemCode", form.get("itemCode"), "name", form.get("name"),
                "category", form.get("category"), "unitOfMeasure", form.get("unitOfMeasure"),
                "description", form.get("description"), "reorderLevel", decimalValue(form, "reorderLevel")), "Supply item registered.");
    }

    public OperationResult receiveStock(Map<String, String> form) {
        return post(inventoryUrl + "/inventory/receive", Map.of(
                "supplyItemId", longValue(form, "supplyItemId"), "location", form.get("location"),
                "quantity", decimalValue(form, "quantity"), "reference", form.get("reference")), "Stock received.");
    }

    public OperationResult createRequest(Map<String, String> form) {
        try {
            Long supplyItemId = longValue(form, "supplyItemId");
            Map<String, Object> supply = getSupplies().stream()
                    .filter(candidate -> idsMatch(candidate.get("id"), supplyItemId))
                    .findFirst()
                    .orElse(Map.of());
            if (supply.isEmpty()) return OperationResult.failure("Choose a valid supply item from the catalog.");

            String description = firstNonBlank(form.get("description"), stringValue(supply.get("name")));
            String unitOfMeasure = firstNonBlank(form.get("unitOfMeasure"), stringValue(supply.get("unitOfMeasure")));
            if (description == null || unitOfMeasure == null) return OperationResult.failure("The selected supply item is missing its description or unit of measure.");

            Map<String, Object> item = new HashMap<>();
            item.put("resourceType", "SUPPLY");
            item.put("supplyItemId", supplyItemId);
            item.put("description", description);
            item.put("quantityRequested", decimalValue(form, "quantity"));
            item.put("unitOfMeasure", unitOfMeasure);

            Map<String, Object> payload = new HashMap<>();
            payload.put("requestNumber", optional(form.get("requestNumber")));
            payload.put("requestingUnitId", longValue(form, "requestingUnitId"));
            payload.put("requestedBy", optional(form.get("requestedBy")));
            payload.put("purpose", firstNonBlank(form.get("purpose"), "Operational supply request"));
            payload.put("priority", firstNonBlank(form.get("priority"), "NORMAL"));
            payload.put("deliveryLocation", optional(form.get("deliveryLocation")));
            payload.put("items", List.of(item));
            return post(requestUrl + "/requests", payload, "Logistics request created and sent to triage.");
        } catch (NumberFormatException exception) {
            return OperationResult.failure("Supply, unit, and quantity must be valid values.");
        }
    }

    public OperationResult approveRequest(Long id, String approverId) {
        return post(requestUrl + "/requests/" + id + "/approve", Map.of(
                "approverId", approverId, "decision", "APPROVED", "comments", "Approved from DEF-LOGIX portal"),
                "Request approved.");
    }

    public OperationResult rejectRequest(Long id, String approverId) {
        return post(requestUrl + "/requests/" + id + "/reject", Map.of(
                "approverId", approverId, "decision", "REJECTED", "comments", "Dismissed from DEF-LOGIX portal"),
                "Request dismissed.");
    }

    public OperationResult allocateRequest(Long id, Map<String, String> form) {
        return post(requestUrl + "/requests/" + id + "/allocate", Map.of(
                "requestItemId", longValue(form, "requestItemId"), "supplyItemId", longValue(form, "supplyItemId"),
                "quantityAllocated", decimalValue(form, "quantityAllocated"), "allocatedBy", form.get("allocatedBy")),
                "Stock allocated.");
    }

    private OperationResult post(String uri, Object payload, String successMessage) {
        try {
            authenticatedPost(uri).body(payload).retrieve().toBodilessEntity();
            return OperationResult.success(successMessage);
        } catch (RestClientResponseException exception) {
            return OperationResult.failure(errorMessage(exception));
        } catch (Exception exception) {
            return OperationResult.failure("Unable to complete the operation: " + exception.getMessage());
        }
    }

    private OperationResult postPublic(String uri, Object payload, String successMessage) {
        try { restClient.post().uri(uri).body(payload).retrieve().toBodilessEntity(); return OperationResult.success(successMessage); }
        catch (RestClientResponseException exception) { return OperationResult.failure(errorMessage(exception)); }
        catch (Exception exception) { return OperationResult.failure("Unable to complete the operation: " + exception.getMessage()); }
    }

    private RestClient.RequestHeadersSpec<?> authenticatedGet(String uri) {
        RestClient.RequestHeadersSpec<?> request = restClient.get().uri(uri);
        String token = accessToken();
        return token == null ? request : request.header("Authorization", "Bearer " + token);
    }
    private RestClient.RequestBodySpec authenticatedPost(String uri) {
        RestClient.RequestBodySpec request = restClient.post().uri(uri);
        String token = accessToken();
        return token == null ? request : request.header("Authorization", "Bearer " + token);
    }
    private RestClient.RequestHeadersSpec<?> authenticatedPatch(String uri) {
        RestClient.RequestHeadersSpec<?> request = restClient.patch().uri(uri);
        String token = accessToken();
        return token == null ? request : request.header("Authorization", "Bearer " + token);
    }
    private String accessToken() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) return null;
        var session = attributes.getRequest().getSession(false);
        return session == null ? null : (String) session.getAttribute("accessToken");
    }
    private String errorMessage(RestClientResponseException exception) {
        String body = exception.getResponseBodyAsString();
        return exception.getStatusCode().value() + " from service" + (body.isBlank() ? "." : ": " + body);
    }
    private String optional(String value) { return value == null ? "" : value.trim(); }
    private String firstNonBlank(String first, String fallback) {
        return first == null || first.isBlank() ? (fallback == null || fallback.isBlank() ? null : fallback.trim()) : first.trim();
    }
    private String stringValue(Object value) { return value == null ? null : String.valueOf(value); }
    private boolean idsMatch(Object value, Long expected) {
        if (value instanceof Number number) return number.longValue() == expected;
        try { return Long.parseLong(String.valueOf(value)) == expected; }
        catch (NumberFormatException ignored) { return false; }
    }

    private Long longValue(Map<String, String> form, String key) { return Long.valueOf(form.get(key)); }
    private BigDecimal decimalValue(Map<String, String> form, String key) { return new BigDecimal(form.get(key)); }
    private void addOptionalLong(Map<String, Object> payload, String key, String value) {
        if (value != null && !value.isBlank()) payload.put(key, Long.valueOf(value));
    }

    public record OperationResult(boolean successful, String message) {
        public static OperationResult success(String message) { return new OperationResult(true, message); }
        public static OperationResult failure(String message) { return new OperationResult(false, message); }
    }
    public record LoginResult(boolean successful, String message, String token, String username, String fullName, String role) {
        static LoginResult success(String token, String username, String fullName, String role) { return new LoginResult(true, "", token, username, fullName, role); }
        static LoginResult failure(String message) { return new LoginResult(false, message, null, null, null, null); }
    }
}

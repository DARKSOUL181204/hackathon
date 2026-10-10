package com.defense.logistics.request.service;
import com.defense.logistics.request.dto.LogisticsRequestDto;
import com.defense.logistics.request.dto.RequestItemDto;
import com.defense.logistics.request.dto.ApprovalDto;
import com.defense.logistics.request.dto.AllocationDto;
import com.defense.logistics.request.model.*;
import com.defense.logistics.request.model.enums.*;
import com.defense.logistics.request.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class RequestOrchestratorService {
    private final LogisticsRequestRepository requestRepo;
    private final RequestItemRepository itemRepo;
    private final ApprovalRecordRepository approvalRepo;
    private final ResourceAllocationRepository allocationRepo;
    private final RequestStatusHistoryRepository historyRepo;
    private final RestClient restClient;

    @Value("${resource.service.url}")
    private String resourceServiceUrl;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Transactional
    public LogisticsRequestDto createRequest(LogisticsRequestDto dto) {
        // 1. Validate unit exists in resource-service using REST
        try {
            restClient.get().uri(resourceServiceUrl + "/units/" + dto.getRequestingUnitId())
                      .header("Authorization", bearerToken())
                      .retrieve().toBodilessEntity();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid requestingUnitId. Does not exist in Resource Service.");
        }

        String requestNumber = dto.getRequestNumber() == null || dto.getRequestNumber().isBlank() ? "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : dto.getRequestNumber();
        LogisticsRequest request = LogisticsRequest.builder()
                .requestNumber(requestNumber).requestingUnitId(dto.getRequestingUnitId())
                .requestedBy(dto.getRequestedBy()).purpose(dto.getPurpose() == null || dto.getPurpose().isBlank() ? "Operational supply request" : dto.getPurpose())
                .priority(dto.getPriority()).requiredByDate(dto.getRequiredByDate())
                .deliveryLocation(dto.getDeliveryLocation()).status(RequestStatus.CREATED)
                .build();
        request = requestRepo.save(request);

        for(RequestItemDto itemDto : dto.getItems()) {
            if (itemDto.getResourceType() == ResourceType.SUPPLY && itemDto.getSupplyItemId() == null) {
                throw new IllegalArgumentException("Supply item ID is required for a SUPPLY request item");
            }
            RequestItem item = RequestItem.builder().logisticsRequest(request)
                    .resourceType(itemDto.getResourceType()).supplyItemId(itemDto.getSupplyItemId())
                    .description(itemDto.getDescription())
                    .quantityRequested(itemDto.getQuantityRequested())
                    .unitOfMeasure(itemDto.getUnitOfMeasure()).specifications(itemDto.getSpecifications())
                    .build();
            itemRepo.save(item);
        }

        recordHistory(request, null, RequestStatus.CREATED, dto.getRequestedBy(), "Initial Creation");
        dto.setId(request.getId());
        dto.setRequestNumber(requestNumber);
        return dto;
    }

    @Transactional
    public void processApproval(Long requestId, ApprovalDto dto) {
        LogisticsRequest request = requestRepo.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (request.getStatus() != RequestStatus.CREATED) throw new IllegalStateException("Request is not in CREATED state");

        RequestStatus newStatus = dto.getDecision().equalsIgnoreCase("APPROVED") ? RequestStatus.APPROVED : RequestStatus.REJECTED;
        
        ApprovalRecord record = ApprovalRecord.builder().logisticsRequest(request)
                .approverId(dto.getApproverId()).decision(dto.getDecision()).comments(dto.getComments()).build();
        approvalRepo.save(record);

        recordHistory(request, request.getStatus(), newStatus, dto.getApproverId(), dto.getComments());
        request.setStatus(newStatus);
        requestRepo.save(request);
    }

    @Transactional
    public void allocateResource(Long requestId, AllocationDto dto) {
        LogisticsRequest request = requestRepo.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (request.getStatus() != RequestStatus.APPROVED && request.getStatus() != RequestStatus.ALLOCATING) {
            throw new IllegalStateException("Request must be APPROVED or ALLOCATING to perform allocations");
        }
        
        if (request.getStatus() == RequestStatus.APPROVED) {
            recordHistory(request, request.getStatus(), RequestStatus.ALLOCATING, dto.getAllocatedBy(), "Starting allocation process");
            request.setStatus(RequestStatus.ALLOCATING);
            requestRepo.save(request);
        }

        RequestItem item = itemRepo.findById(dto.getRequestItemId()).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!item.getLogisticsRequest().getId().equals(request.getId())) {
            throw new IllegalArgumentException("Request item does not belong to this request");
        }
        
        // 2. Orchestrate call to inventory-service if it's a SUPPLY
        if(item.getResourceType() == ResourceType.SUPPLY) {
            if(dto.getSupplyItemId() == null) throw new IllegalArgumentException("Supply item ID required");
            if (!dto.getSupplyItemId().equals(item.getSupplyItemId())) {
                throw new IllegalArgumentException("Allocated supply item does not match the requested supply item");
            }
            // Synchronous call to lock/issue stock in Inventory DB
            try {
                restClient.post().uri(inventoryServiceUrl + "/inventory/issue")
                          .header("Authorization", bearerToken())
                          .body(Map.of(
                              "supplyItemId", dto.getSupplyItemId(),
                              "location", request.getDeliveryLocation(),
                              "requestId", request.getId(),
                              "quantity", dto.getQuantityAllocated(),
                              "issuedTo", request.getRequestedBy()
                          ))
                          .retrieve().toBodilessEntity();
            } catch (Exception e) {
                // Compensation or Failure State
                recordHistory(request, request.getStatus(), RequestStatus.ALLOCATION_FAILED, dto.getAllocatedBy(), "Failed downstream supply issue: " + e.getMessage());
                request.setStatus(RequestStatus.ALLOCATION_FAILED);
                requestRepo.save(request);
                throw new IllegalStateException("Cross-service allocation failed: " + e.getMessage());
            }
        }

        ResourceAllocation allocation = ResourceAllocation.builder()
                .requestItem(item).resourceType(item.getResourceType())
                .resourceId(dto.getResourceId()).supplyItemId(dto.getSupplyItemId())
                .quantityAllocated(dto.getQuantityAllocated()).status(AllocationStatus.ALLOCATED)
                .allocatedBy(dto.getAllocatedBy()).build();
        allocationRepo.save(allocation);
        recordHistory(request, request.getStatus(), RequestStatus.ALLOCATED, dto.getAllocatedBy(), "Allocation completed");
        request.setStatus(RequestStatus.ALLOCATED);
        requestRepo.save(request);
    }

    private void recordHistory(LogisticsRequest request, RequestStatus prev, RequestStatus next, String by, String reason) {
        RequestStatusHistory history = RequestStatusHistory.builder().logisticsRequest(request)
                .previousStatus(prev).newStatus(next).changedBy(by).reason(reason).build();
        historyRepo.save(history);
    }
    private String bearerToken() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String header = attributes.getRequest().getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) return header;
        }
        throw new IllegalStateException("No authenticated service token is available for the downstream request.");
    }
    @Transactional(readOnly = true)
    public List<LogisticsRequestDto> getAll() {
        return requestRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LogisticsRequestDto getById(Long id) {
        return toDto(requestRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Request not found")));
    }

    private LogisticsRequestDto toDto(LogisticsRequest request) {
        LogisticsRequestDto dto = new LogisticsRequestDto();
        dto.setId(request.getId());
        dto.setRequestNumber(request.getRequestNumber());
        dto.setRequestingUnitId(request.getRequestingUnitId());
        dto.setRequestedBy(request.getRequestedBy());
        dto.setPurpose(request.getPurpose());
        dto.setPriority(request.getPriority());
        dto.setRequiredByDate(request.getRequiredByDate());
        dto.setDeliveryLocation(request.getDeliveryLocation());
        dto.setStatus(request.getStatus());
        dto.setItems(itemRepo.findByLogisticsRequestId(request.getId()).stream().map(item -> {
            RequestItemDto itemDto = new RequestItemDto();
            itemDto.setId(item.getId());
            itemDto.setResourceType(item.getResourceType());
            itemDto.setSupplyItemId(item.getSupplyItemId());
            itemDto.setDescription(item.getDescription());
            itemDto.setQuantityRequested(item.getQuantityRequested());
            itemDto.setUnitOfMeasure(item.getUnitOfMeasure());
            itemDto.setSpecifications(item.getSpecifications());
            return itemDto;
        }).toList());
        return dto;
    }
}

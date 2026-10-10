package com.defense.logistics.resource.dto;

import com.defense.logistics.resource.model.enums.ApplicationRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccessRequestSubmission {
    @NotBlank @Size(max = 255) private String fullName;
    @NotBlank @Size(min = 3, max = 100) private String username;
    @NotBlank @Size(min = 10, max = 128) private String password;
    @NotNull private ApplicationRole requestedRole;
    @Size(max = 500) private String requestDetails;
}

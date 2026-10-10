package com.defense.logistics.resource;

import com.defense.logistics.resource.service.AccessManagementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResourceApplication {

    @org.springframework.context.annotation.Bean
    CommandLineRunner bootstrapAdministrator(AccessManagementService accessManagementService) {
        return ignored -> accessManagementService.ensureBootstrapAdmin();
    }
    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }
}

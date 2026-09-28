package com.granttrack.granttrack.controller;

import com.granttrack.granttrack.dto.GrantUtilizationResponse;
import com.granttrack.granttrack.entity.Application;
import com.granttrack.granttrack.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<Application> createApplication(
            @Valid @RequestBody Application application) {

        return ResponseEntity.ok(
                applicationService.createApplication(application));
    }

    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {

        return ResponseEntity.ok(
                applicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getApplicationById(id));
    }

    @GetMapping("/{id}/utilization")
    public ResponseEntity<GrantUtilizationResponse> getGrantUtilization(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getGrantUtilization(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Double approvedAmount) {

        return ResponseEntity.ok(
                applicationService.updateApplicationStatus(
                        id,
                        status,
                        approvedAmount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(
            @PathVariable Long id) {

        applicationService.deleteApplication(id);

        return ResponseEntity.ok(
                "Application deleted successfully");
    }
}
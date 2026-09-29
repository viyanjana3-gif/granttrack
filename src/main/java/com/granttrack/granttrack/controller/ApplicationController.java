package com.granttrack.granttrack.controller;

import com.granttrack.granttrack.dto.GrantUtilizationResponse;
import com.granttrack.granttrack.dto.AuthUser;
import com.granttrack.granttrack.entity.Application;
import com.granttrack.granttrack.service.ApplicationService;
import com.granttrack.granttrack.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //request and returns json response
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    @PostMapping //create new record in the database
    public ResponseEntity<Application> createApplication (
                        @Valid @RequestBody Application application,
                        @RequestAttribute(AuthService.USER_ATTRIBUTE) AuthUser user) {

        return ResponseEntity.ok(
                                applicationService.createApplication(application, user));
    }

    @GetMapping //retrieve all applications
        public ResponseEntity<List<Application>> getAllApplications(
                        @RequestAttribute(AuthService.USER_ATTRIBUTE) AuthUser user) //retrieve all applications
    {

        return ResponseEntity.ok(
                                applicationService.getAllApplications(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(
                        @PathVariable Long id,
                        @RequestAttribute(AuthService.USER_ATTRIBUTE) AuthUser user) {

        return ResponseEntity.ok(
                                applicationService.getApplicationById(id, user));
    }

    @GetMapping("/{id}/utilization")
    public ResponseEntity<GrantUtilizationResponse> getGrantUtilization(
                        @PathVariable Long id,
                        @RequestAttribute(AuthService.USER_ATTRIBUTE) AuthUser user) {

        return ResponseEntity.ok(
                                applicationService.getGrantUtilization(id, user));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Double approvedAmount,
            @RequestAttribute(AuthService.USER_ATTRIBUTE) AuthUser user) {

        return ResponseEntity.ok(
                applicationService.updateApplicationStatus(
                        id,
                        status,
                        approvedAmount,
                        user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(
            @PathVariable Long id,
            @RequestAttribute(AuthService.USER_ATTRIBUTE) AuthUser user) {

        applicationService.deleteApplication(id, user);

        return ResponseEntity.ok(
                "Application deleted successfully");
    }
}
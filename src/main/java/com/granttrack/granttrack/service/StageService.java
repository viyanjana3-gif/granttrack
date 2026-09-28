package com.granttrack.granttrack.service;

import com.granttrack.granttrack.entity.Application;
import com.granttrack.granttrack.entity.Stage;
import com.granttrack.granttrack.repository.ApplicationRepository;
import com.granttrack.granttrack.repository.StageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ApplicationRepository applicationRepository;

    public StageService(
            StageRepository stageRepository,
            ApplicationRepository applicationRepository) {

        this.stageRepository = stageRepository;
        this.applicationRepository = applicationRepository;
    }

    public Stage addStage(Long applicationId, Stage stage) {

        // Find application
        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Application not found"));

        // Get stage name
        String stageName = stage.getStageName();

        // Validate stage
        if (stageName == null ||
                (!stageName.equalsIgnoreCase("SUBMITTED") &&
                 !stageName.equalsIgnoreCase("UNDER_REVIEW") &&
                 !stageName.equalsIgnoreCase("APPROVED") &&
                 !stageName.equalsIgnoreCase("REJECTED"))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid stage. Use SUBMITTED, UNDER_REVIEW, APPROVED or REJECTED");
        }

        // Convert to uppercase
        stageName = stageName.toUpperCase();

        // APPROVED validation
        if ("APPROVED".equals(stageName)) {

            if (application.getApprovedAmount() == null ||
                    application.getApprovedAmount() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Approved amount must be set before approving application");
            }

            // Approved amount cannot be greater than requested amount
            if (application.getRequestedAmount() != null &&
                    application.getApprovedAmount()
                            > application.getRequestedAmount()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Approved amount cannot be greater than requested amount");
            }
        }

        // Connect stage with application
        stage.setApplication(application);

        // Update application status
        application.setStatus(stageName);

        // Save application
        applicationRepository.save(application);

        // Save stage
        return stageRepository.save(stage);
    }

    public List<Stage> getAllStages() {

        return stageRepository.findAll();
    }

    public Stage getStageById(Long id) {

        return stageRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Stage not found"));
    }
}
package com.granttrack.granttrack.service;

import com.granttrack.granttrack.dto.GrantUtilizationResponse;
import com.granttrack.granttrack.entity.Application;
import com.granttrack.granttrack.entity.Expenditure;
import com.granttrack.granttrack.repository.ApplicationRepository;
import com.granttrack.granttrack.repository.ExpenditureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ExpenditureRepository expenditureRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ExpenditureRepository expenditureRepository) {

        this.applicationRepository = applicationRepository;
        this.expenditureRepository = expenditureRepository;
    }

    public Application createApplication(Application application) {

        if (application.getStatus() == null ||
                application.getStatus().isBlank()) {

            application.setStatus("SUBMITTED");
        }

        return applicationRepository.save(application);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplicationById(Long id) {

        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));
    }

    public Application updateApplicationStatus(
            Long id,
            String status,
            Double approvedAmount) {

        Application application = getApplicationById(id);

        application.setStatus(status);

        if ("APPROVED".equalsIgnoreCase(status)) {

            if (approvedAmount == null || approvedAmount <= 0) {

                throw new RuntimeException(
                        "Approved amount must be greater than zero");
            }

            application.setApprovedAmount(approvedAmount);
        }

        return applicationRepository.save(application);
    }

    public GrantUtilizationResponse getGrantUtilization(Long applicationId) {

        Application application = getApplicationById(applicationId);

        if (application.getApprovedAmount() == null) {

            throw new RuntimeException(
                    "Approved amount is not available for this application");
        }

        List<Expenditure> expenditures =
                expenditureRepository.findByApplicationId(applicationId);

        double totalSpent = expenditures.stream()
                .mapToDouble(Expenditure::getAmount)
                .sum();

        double approvedAmount = application.getApprovedAmount();

        double remainingAmount =
                approvedAmount - totalSpent;

        double utilizationPercentage =
                (totalSpent / approvedAmount) * 100;

        return new GrantUtilizationResponse(
                applicationId,
                approvedAmount,
                totalSpent,
                remainingAmount,
                utilizationPercentage
        );
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
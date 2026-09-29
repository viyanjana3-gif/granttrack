package com.granttrack.granttrack.service;

import com.granttrack.granttrack.dto.GrantUtilizationResponse;
import com.granttrack.granttrack.dto.AuthUser;
import com.granttrack.granttrack.entity.Application;
import com.granttrack.granttrack.entity.Expenditure;
import com.granttrack.granttrack.entity.Faculty;
import com.granttrack.granttrack.repository.ApplicationRepository;
import com.granttrack.granttrack.repository.ExpenditureRepository;
import com.granttrack.granttrack.repository.FacultyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ExpenditureRepository expenditureRepository;
    private final FacultyRepository facultyRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ExpenditureRepository expenditureRepository,
            FacultyRepository facultyRepository) {

        this.applicationRepository = applicationRepository;
        this.expenditureRepository = expenditureRepository;
        this.facultyRepository = facultyRepository;
    }

    public Application createApplication(Application application, AuthUser user) {
        application.setId(null);

        if (user.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Administrators cannot create applications");
        }

        Faculty faculty = facultyRepository.findById(user.facultyId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Faculty identity is unavailable"));
        application.setFaculty(faculty);
        application.setStatus("SUBMITTED");
        application.setApprovedAmount(null);
        application.setStages(new ArrayList<>());
        application.setExpenditures(new ArrayList<>());

        return applicationRepository.save(application);
    }

    public List<Application> getAllApplications(AuthUser user) {
        if (user.isAdmin()) {
            return applicationRepository.findAll();
        }
        return applicationRepository.findByFaculty_Id(user.facultyId());
    }

    public Application getApplicationById(Long id) {

        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));
    }

    public Application getApplicationById(Long id, AuthUser user) {
        Application application = getApplicationById(id);
        verifyOwnership(application, user);
        return application;
    }

    public Application updateApplicationStatus(
            Long id,
            String status,
            Double approvedAmount,
            AuthUser user) {

        if (user.isFaculty()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Administrator access required");
        }

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

    public GrantUtilizationResponse getGrantUtilization(
            Long applicationId,
            AuthUser user) {

        Application application = getApplicationById(applicationId, user);

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

    public void deleteApplication(Long id, AuthUser user) {
        if (user.isFaculty()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Administrator access required");
        }
        applicationRepository.deleteById(id);
    }

    private void verifyOwnership(Application application, AuthUser user) {
        if (user.isFaculty() &&
                (application.getFaculty() == null ||
                 !user.facultyId().equals(application.getFaculty().getId()))) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Application not found");
        }
    }
}
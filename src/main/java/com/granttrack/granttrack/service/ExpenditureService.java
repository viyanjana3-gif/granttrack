package com.granttrack.granttrack.service;

import com.granttrack.granttrack.dto.AuthUser;
import com.granttrack.granttrack.entity.Application;
import com.granttrack.granttrack.entity.Expenditure;
import com.granttrack.granttrack.repository.ApplicationRepository;
import com.granttrack.granttrack.repository.ExpenditureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final ApplicationRepository applicationRepository;

    public ExpenditureService(
            ExpenditureRepository expenditureRepository,
            ApplicationRepository applicationRepository) {

        this.expenditureRepository = expenditureRepository;
        this.applicationRepository = applicationRepository;
    }

    public Expenditure addExpenditure(
            Long applicationId,
            Expenditure expenditure,
            AuthUser user) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Application not found"));

        // Faculty can only add expenditures to their own applications
        if (user.isFaculty() &&
                (application.getFaculty() == null ||
                 !user.facultyId().equals(application.getFaculty().getId()))) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Application not found");
        }

        if (!"APPROVED".equalsIgnoreCase(application.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expenditure can only be added to an approved application.");
        }

        if (application.getApprovedAmount() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Approved amount is not set for this application.");
        }

        List<Expenditure> existingExpenses =
                expenditureRepository.findByApplicationId(applicationId);

        double totalAlreadySpent = existingExpenses.stream()
                .mapToDouble(Expenditure::getAmount)
                .sum();

        double remaining = application.getApprovedAmount() - totalAlreadySpent;
        double newTotal = totalAlreadySpent + expenditure.getAmount();

        if (newTotal > application.getApprovedAmount()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Limit exceeded! You can only spend \u20b9" +
                    String.format("%.2f", remaining) +
                    " more. Approved: \u20b9" +
                    String.format("%.2f", application.getApprovedAmount()) +
                    ", Already spent: \u20b9" +
                    String.format("%.2f", totalAlreadySpent) + ".");
        }

        expenditure.setApplication(application);
        return expenditureRepository.save(expenditure);
    }

    public List<Expenditure> getAllExpenditures() {
        return expenditureRepository.findAll();
    }

    public List<Expenditure> getExpendituresByApplication(Long applicationId) {
        return expenditureRepository.findByApplicationId(applicationId);
    }

    public Expenditure getExpenditureById(Long id) {
        return expenditureRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Expenditure not found"));
    }
}
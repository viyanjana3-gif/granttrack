package com.granttrack.granttrack.service;

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
            Expenditure expenditure) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Application not found"));

        // BUSINESS RULE 1
        // Expenditure is allowed only for APPROVED applications.

        if (!"APPROVED".equalsIgnoreCase(application.getStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expenditure logging is only allowed for approved applications.");
        }

        if (application.getApprovedAmount() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Approved amount is not available.");
        }

        // Calculate existing expenditure

        List<Expenditure> existingExpenses =
                expenditureRepository.findByApplicationId(applicationId);

        double totalAlreadySpent = existingExpenses.stream()
                .mapToDouble(Expenditure::getAmount)
                .sum();

        double newTotal =
                totalAlreadySpent + expenditure.getAmount();

        // BUSINESS RULE 2
        // Total expenditure cannot exceed approved amount.

        if (newTotal > application.getApprovedAmount()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Total expenditure cannot exceed the approved grant amount. " +
                    "Approved amount: " + application.getApprovedAmount() +
                    ", Already spent: " + totalAlreadySpent +
                    ", New expenditure: " + expenditure.getAmount());
        }

        expenditure.setApplication(application);

        return expenditureRepository.save(expenditure);
    }

    public List<Expenditure> getAllExpenditures() {
        return expenditureRepository.findAll();
    }

    public List<Expenditure> getExpendituresByApplication(
            Long applicationId) {

        return expenditureRepository
                .findByApplicationId(applicationId);
    }

    public Expenditure getExpenditureById(Long id) {

        return expenditureRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Expenditure not found"));
    }
}
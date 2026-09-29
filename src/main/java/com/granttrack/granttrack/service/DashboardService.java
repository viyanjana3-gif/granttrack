package com.granttrack.granttrack.service;

import com.granttrack.granttrack.dto.DashboardResponse;
import com.granttrack.granttrack.entity.Application;
import com.granttrack.granttrack.entity.Expenditure;
import com.granttrack.granttrack.repository.ApplicationRepository;
import com.granttrack.granttrack.repository.ExpenditureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final ApplicationRepository applicationRepository;
    private final ExpenditureRepository expenditureRepository;

    public DashboardService(
            ApplicationRepository applicationRepository,
            ExpenditureRepository expenditureRepository) {

        this.applicationRepository = applicationRepository;
        this.expenditureRepository = expenditureRepository;
    }

    public DashboardResponse getDashboardSummary() {

        List<Application> applications =
                applicationRepository.findAll();

        long totalApplications = applications.size();

        long approvedApplications = applications.stream()
                .filter(application ->
                        "APPROVED".equalsIgnoreCase(
                                application.getStatus()))
                .count();

        long rejectedApplications = applications.stream()
                .filter(application ->
                        "REJECTED".equalsIgnoreCase(
                                application.getStatus()))
                .count();

        double totalApprovedGrant = applications.stream()
                .filter(application ->
                        "APPROVED".equalsIgnoreCase(
                                application.getStatus()))
                .map(Application::getApprovedAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        List<Expenditure> expenditures =
                expenditureRepository.findByApplication_StatusIgnoreCase(
                        "APPROVED");

        double totalExpenditure = expenditures.stream()
                .map(Expenditure::getAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        double totalRemainingGrant =
                totalApprovedGrant - totalExpenditure;

        return new DashboardResponse(
                totalApplications,
                approvedApplications,
                rejectedApplications,
                totalApprovedGrant,
                totalExpenditure,
                totalRemainingGrant
        );
    }
}
package com.granttrack.granttrack.dto;

public class GrantUtilizationResponse {

    private Long applicationId;
    private Double approvedAmount;
    private Double totalSpent;
    private Double remainingAmount;
    private Double utilizationPercentage;

    public GrantUtilizationResponse() {
    }

    public GrantUtilizationResponse(
            Long applicationId,
            Double approvedAmount,
            Double totalSpent,
            Double remainingAmount,
            Double utilizationPercentage) {

        this.applicationId = applicationId;
        this.approvedAmount = approvedAmount;
        this.totalSpent = totalSpent;
        this.remainingAmount = remainingAmount;
        this.utilizationPercentage = utilizationPercentage;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }
}
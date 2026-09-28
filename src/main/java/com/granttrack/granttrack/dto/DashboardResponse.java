package com.granttrack.granttrack.dto;

public class DashboardResponse {

    private long totalApplications;
    private long approvedApplications;
    private long rejectedApplications;

    private double totalApprovedGrant;
    private double totalExpenditure;
    private double totalRemainingGrant;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalApplications,
            long approvedApplications,
            long rejectedApplications,
            double totalApprovedGrant,
            double totalExpenditure,
            double totalRemainingGrant) {

        this.totalApplications = totalApplications;
        this.approvedApplications = approvedApplications;
        this.rejectedApplications = rejectedApplications;
        this.totalApprovedGrant = totalApprovedGrant;
        this.totalExpenditure = totalExpenditure;
        this.totalRemainingGrant = totalRemainingGrant;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getApprovedApplications() {
        return approvedApplications;
    }

    public void setApprovedApplications(long approvedApplications) {
        this.approvedApplications = approvedApplications;
    }

    public long getRejectedApplications() {
        return rejectedApplications;
    }

    public void setRejectedApplications(long rejectedApplications) {
        this.rejectedApplications = rejectedApplications;
    }

    public double getTotalApprovedGrant() {
        return totalApprovedGrant;
    }

    public void setTotalApprovedGrant(double totalApprovedGrant) {
        this.totalApprovedGrant = totalApprovedGrant;
    }

    public double getTotalExpenditure() {
        return totalExpenditure;
    }

    public void setTotalExpenditure(double totalExpenditure) {
        this.totalExpenditure = totalExpenditure;
    }

    public double getTotalRemainingGrant() {
        return totalRemainingGrant;
    }

    public void setTotalRemainingGrant(double totalRemainingGrant) {
        this.totalRemainingGrant = totalRemainingGrant;
    }
}
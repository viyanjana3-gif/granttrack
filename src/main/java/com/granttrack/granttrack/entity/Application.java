package com.granttrack.granttrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Requested amount is required")
    @Positive(message = "Requested amount must be positive")
    private Double requestedAmount;

    @NotBlank(message = "Abstract is required")
    @Column(length = 2000)
    private String abstractText;

    private String status;

    private Double approvedAmount;

    private String utilizationDeadline;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    @JsonIgnoreProperties({"applications"})
    private Faculty faculty;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"application"})
    private List<Stage> stages = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"application"})
    private List<Expenditure> expenditures = new ArrayList<>();

    public Application() {
        this.status = "SUBMITTED";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getUtilizationDeadline() {
        return utilizationDeadline;
    }

    public void setUtilizationDeadline(String utilizationDeadline) {
        this.utilizationDeadline = utilizationDeadline;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public List<Expenditure> getExpenditures() {
        return expenditures;
    }

    public void setExpenditures(List<Expenditure> expenditures) {
        this.expenditures = expenditures;
    }
}
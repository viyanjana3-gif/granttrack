package com.granttrack.granttrack.controller;

import com.granttrack.granttrack.entity.Expenditure;
import com.granttrack.granttrack.service.ExpenditureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(
            ExpenditureService expenditureService) {

        this.expenditureService = expenditureService;
    }

    // ==============================
    // ADD EXPENDITURE
    // ==============================

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<Expenditure> addExpenditure(
            @PathVariable Long applicationId,
            @Valid @RequestBody Expenditure expenditure) {

        Expenditure savedExpenditure =
                expenditureService.addExpenditure(
                        applicationId,
                        expenditure);

        return ResponseEntity.ok(savedExpenditure);
    }


    // ==============================
    // GET ALL EXPENDITURES
    // ==============================

    @GetMapping
    public ResponseEntity<List<Expenditure>> getAllExpenditures() {

        List<Expenditure> expenditures =
                expenditureService.getAllExpenditures();

        return ResponseEntity.ok(expenditures);
    }


    // ==============================
    // GET EXPENDITURES BY APPLICATION
    // ==============================

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Expenditure>>
    getExpendituresByApplication(
            @PathVariable Long applicationId) {

        List<Expenditure> expenditures =
                expenditureService
                        .getExpendituresByApplication(applicationId);

        return ResponseEntity.ok(expenditures);
    }


    // ==============================
    // GET EXPENDITURE BY ID
    // ==============================

    @GetMapping("/{id}")
    public ResponseEntity<Expenditure> getExpenditureById(
            @PathVariable Long id) {

        Expenditure expenditure =
                expenditureService
                        .getExpenditureById(id);

        return ResponseEntity.ok(expenditure);
    }
}
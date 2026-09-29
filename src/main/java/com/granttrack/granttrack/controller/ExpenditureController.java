package com.granttrack.granttrack.controller;

import com.granttrack.granttrack.dto.AuthUser;
import com.granttrack.granttrack.entity.Expenditure;
import com.granttrack.granttrack.service.AuthService;
import com.granttrack.granttrack.service.ExpenditureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<Expenditure> addExpenditure(
            @PathVariable Long applicationId,
            @Valid @RequestBody Expenditure expenditure,
            @RequestAttribute(AuthService.USER_ATTRIBUTE) AuthUser user) {

        return ResponseEntity.ok(
                expenditureService.addExpenditure(applicationId, expenditure, user));
    }

    @GetMapping
    public ResponseEntity<List<Expenditure>> getAllExpenditures() {
        return ResponseEntity.ok(expenditureService.getAllExpenditures());
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Expenditure>> getExpendituresByApplication(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(
                expenditureService.getExpendituresByApplication(applicationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expenditure> getExpenditureById(@PathVariable Long id) {
        return ResponseEntity.ok(expenditureService.getExpenditureById(id));
    }
}
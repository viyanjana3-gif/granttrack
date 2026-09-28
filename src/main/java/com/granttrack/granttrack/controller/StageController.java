package com.granttrack.granttrack.controller;

import com.granttrack.granttrack.entity.Stage;
import com.granttrack.granttrack.service.StageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<Stage> addStage(
            @PathVariable Long applicationId,
            @Valid @RequestBody Stage stage) {

        return ResponseEntity.ok(
                stageService.addStage(
                        applicationId,
                        stage));
    }

    @GetMapping
    public ResponseEntity<List<Stage>> getAllStages() {

        return ResponseEntity.ok(
                stageService.getAllStages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stage> getStageById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                stageService.getStageById(id));
    }
}
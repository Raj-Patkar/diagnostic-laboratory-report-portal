package com.diaglab.portal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diaglab.portal.entity.LabTest;
import com.diaglab.portal.entity.TestStatus;
import com.diaglab.portal.service.TestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "http://localhost:5173")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping
    public ResponseEntity<?> createTest(
            @Valid @RequestBody LabTest test
    ) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(testService.createTest(test));

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<LabTest>> getTests(
            @RequestParam(required = false) TestStatus status,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(
                testService.getTests(status, search)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTest(
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(
                    testService.getTestById(id)
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabTest>> getTestsByPatient(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(
                testService.getTestsByPatient(patientId)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam TestStatus status
    ) {
        try {
            return ResponseEntity.ok(
                    testService.updateStatus(id, status)
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
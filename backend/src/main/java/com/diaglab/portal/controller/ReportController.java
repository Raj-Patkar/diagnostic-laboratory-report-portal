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

import com.diaglab.portal.entity.Report;
import com.diaglab.portal.service.ReportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @Valid @RequestBody Report report
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(reportService.createReport(report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Report>> getReports(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(reportService.getReports(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reportService.getReportById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody Report report
    ) {
        try {
            return ResponseEntity.ok(
                    reportService.updateReport(id, report)
            );
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();

            if (message != null && message.startsWith("Report not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(message);
            }

            return ResponseEntity.badRequest().body(message);
        }
    }
}
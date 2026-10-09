
package com.diaglab.portal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.diaglab.portal.service.ActivityService;
import com.diaglab.portal.service.ReportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class ReportController {

    private final ReportService reportService;
    private final ActivityService activityService;

    public ReportController(
            ReportService reportService,
            ActivityService activityService
    ) {
        this.reportService = reportService;
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @Valid @RequestBody Report report,
            Authentication authentication
    ) {
        try {
            Report createdReport = reportService.createReport(report);

            activityService.record(
                    "REPORT_CREATED",
                    authentication.getName(),
                    "Report ID: " + createdReport.getId()
                            + ", Test ID: " + createdReport.getTestId()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(createdReport);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Report>> getReports(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
                reportService.getReports(status)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(
                    reportService.getReportById(id)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody Report report,
            Authentication authentication
    ) {
        try {
            Report updatedReport =
                    reportService.updateReport(id, report);

            activityService.record(
                    "REPORT_UPDATED",
                    authentication.getName(),
                    "Report ID: " + updatedReport.getId()
                            + ", Status: " + updatedReport.getStatus()
            );

            return ResponseEntity.ok(updatedReport);

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();

            if (message != null
                    && message.startsWith("Report not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(message);
            }

            return ResponseEntity
                    .badRequest()
                    .body(message);
        }
    }
}

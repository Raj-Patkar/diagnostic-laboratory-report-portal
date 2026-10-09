package com.diaglab.portal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diaglab.portal.entity.LabTest;
import com.diaglab.portal.entity.Report;
import com.diaglab.portal.entity.TestStatus;
import com.diaglab.portal.repository.LabTestRepository;
import com.diaglab.portal.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final LabTestRepository labTestRepository;

    public ReportService(
            ReportRepository reportRepository,
            LabTestRepository labTestRepository
    ) {
        this.reportRepository = reportRepository;
        this.labTestRepository = labTestRepository;
    }

    @Transactional
    public Report createReport(Report report) {

        LabTest test = labTestRepository.findById(report.getTestId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Test not found with id: " + report.getTestId()
                        )
                );

        if (reportRepository.existsByTestId(report.getTestId())) {
            throw new IllegalArgumentException(
                    "A report already exists for test id: " + report.getTestId()
            );
        }

        if (test.getStatus() != TestStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "The test must be COMPLETED before a report can be created"
            );
        }

        report.setStatus("DRAFT");
        return reportRepository.save(report);
    }

    public List<Report> getReports(String status) {

        if (status != null && !status.isBlank()) {
            return reportRepository.findByStatusIgnoreCase(status.trim());
        }

        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Report not found with id: " + id
                        )
                );
    }

    @Transactional
    public Report updateReport(Long id, Report updatedReport) {

        Report existing = getReportById(id);

        if (updatedReport.getTestId() != null
                && !existing.getTestId().equals(updatedReport.getTestId())) {
            throw new IllegalArgumentException(
                    "A report cannot be moved to another test"
            );
        }

        existing.setResult(updatedReport.getResult());
        existing.setReferenceRange(updatedReport.getReferenceRange());
        existing.setRemarks(updatedReport.getRemarks());

        if (updatedReport.getStatus() != null) {
            String status = updatedReport.getStatus().trim().toUpperCase();

            if (!status.equals("DRAFT") && !status.equals("FINAL")) {
                throw new IllegalArgumentException(
                        "Report status must be DRAFT or FINAL"
                );
            }

            existing.setStatus(status);
        }

        return reportRepository.save(existing);
    }
}
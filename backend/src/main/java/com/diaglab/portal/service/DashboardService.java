package com.diaglab.portal.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.diaglab.portal.entity.TestStatus;
import com.diaglab.portal.repository.ActivityLogRepository;
import com.diaglab.portal.repository.LabTestRepository;
import com.diaglab.portal.repository.PatientRepository;
import com.diaglab.portal.repository.ReportRepository;

@Service
public class DashboardService {

    private final PatientRepository patientRepository;
    private final LabTestRepository testRepository;
    private final ReportRepository reportRepository;
    private final ActivityLogRepository activityRepository;

    public DashboardService(
            PatientRepository patientRepository,
            LabTestRepository testRepository,
            ReportRepository reportRepository,
            ActivityLogRepository activityRepository
    ) {
        this.patientRepository = patientRepository;
        this.testRepository = testRepository;
        this.reportRepository = reportRepository;
        this.activityRepository = activityRepository;
    }

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        summary.put("totalPatients", patientRepository.count());
        summary.put("totalTests", testRepository.count());
        summary.put(
                "pendingTests",
                testRepository.findByStatus(TestStatus.PENDING).size()
        );
        summary.put(
                "inProgressTests",
                testRepository.findByStatus(TestStatus.IN_PROGRESS).size()
        );
        summary.put(
                "completedTests",
                testRepository.findByStatus(TestStatus.COMPLETED).size()
        );
        summary.put("totalReports", reportRepository.count());
        summary.put("finalReports", reportRepository.findByStatusIgnoreCase("FINAL").size());
        summary.put("totalActivities", activityRepository.count());

        return summary;
    }
}
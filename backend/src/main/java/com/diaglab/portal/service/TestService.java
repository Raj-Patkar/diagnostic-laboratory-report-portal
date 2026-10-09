package com.diaglab.portal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.diaglab.portal.entity.LabTest;
import com.diaglab.portal.entity.TestStatus;
import com.diaglab.portal.repository.LabTestRepository;

@Service
public class TestService {

    private final LabTestRepository labTestRepository;

    public TestService(LabTestRepository labTestRepository) {
        this.labTestRepository = labTestRepository;
    }

    public LabTest createTest(LabTest test) {
        return labTestRepository.save(test);
    }

    public List<LabTest> getTests(TestStatus status, String search) {

        if (status != null) {
            return labTestRepository.findByStatus(status);
        }

        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.trim();

            return labTestRepository
                    .findByTestNameContainingIgnoreCaseOrTestTypeContainingIgnoreCase(
                            keyword,
                            keyword
                    );
        }

        return labTestRepository.findAll();
    }

    public LabTest getTestById(Long id) {
        return labTestRepository.findById(id)
                .orElseThrow(()
                        -> new RuntimeException("Test not found with id: " + id)
                );
    }

    public List<LabTest> getTestsByPatient(Long patientId) {
        return labTestRepository.findByPatientId(patientId);
    }

    public LabTest updateStatus(Long id, TestStatus status) {

        LabTest test = getTestById(id);

        test.setStatus(status);

        if (status == TestStatus.COMPLETED) {
            test.setCompletedAt(LocalDateTime.now());
        } else {
            test.setCompletedAt(null);
        }

        return labTestRepository.save(test);
    }
}

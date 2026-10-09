package com.diaglab.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diaglab.portal.entity.LabTest;
import com.diaglab.portal.entity.TestStatus;

public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    List<LabTest> findByStatus(TestStatus status);

    List<LabTest> findByPatientId(Long patientId);

    List<LabTest> findByTestNameContainingIgnoreCaseOrTestTypeContainingIgnoreCase(
            String testName,
            String testType
    );
}
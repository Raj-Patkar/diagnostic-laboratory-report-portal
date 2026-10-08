package com.diaglab.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diaglab.portal.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByPatientCodeIgnoreCase(String patientCode);

    List<Patient> findByPatientCodeContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String patientCode,
            String fullName
    );
}
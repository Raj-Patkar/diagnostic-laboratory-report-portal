package com.diaglab.portal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.diaglab.portal.entity.Patient;
import com.diaglab.portal.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(Patient patient) {

        if (patientRepository.existsByPatientCodeIgnoreCase(patient.getPatientCode())) {
            throw new IllegalArgumentException(
                    "Patient with code " + patient.getPatientCode() + " already exists"
            );
        }

        return patientRepository.save(patient);
    }

    public List<Patient> getPatients(String search) {

        if (search == null || search.trim().isEmpty()) {
            return patientRepository.findAll();
        }

        String keyword = search.trim();

        return patientRepository
                .findByPatientCodeContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        keyword,
                        keyword
                );
    }

    public Patient getPatientById(Long id) {

        return patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found with id: " + id)
                );
    }

    public Patient updatePatient(Long id, Patient updatedPatient) {

        Patient existingPatient = getPatientById(id);

        if (!existingPatient.getPatientCode()
                .equalsIgnoreCase(updatedPatient.getPatientCode())
                && patientRepository.existsByPatientCodeIgnoreCase(
                        updatedPatient.getPatientCode())) {

            throw new IllegalArgumentException(
                    "Patient with code " + updatedPatient.getPatientCode() + " already exists"
            );
        }

        existingPatient.setPatientCode(updatedPatient.getPatientCode());
        existingPatient.setFullName(updatedPatient.getFullName());
        existingPatient.setDateOfBirth(updatedPatient.getDateOfBirth());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setPhone(updatedPatient.getPhone());
        existingPatient.setEmail(updatedPatient.getEmail());
        existingPatient.setAddress(updatedPatient.getAddress());

        return patientRepository.save(existingPatient);
    }
}
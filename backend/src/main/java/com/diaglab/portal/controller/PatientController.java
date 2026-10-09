
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

import com.diaglab.portal.entity.Patient;
import com.diaglab.portal.service.ActivityService;
import com.diaglab.portal.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class PatientController {

    private final PatientService patientService;
    private final ActivityService activityService;

    public PatientController(
            PatientService patientService,
            ActivityService activityService
    ) {
        this.patientService = patientService;
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<?> createPatient(
            @Valid @RequestBody Patient patient,
            Authentication authentication
    ) {
        try {
            Patient createdPatient =
                    patientService.createPatient(patient);

            String username = authentication.getName();

            activityService.record(
                    "PATIENT_CREATED",
                    username,
                    "Patient code: " + createdPatient.getPatientCode()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(createdPatient);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getPatients(
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(
                patientService.getPatients(search)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(
                    patientService.getPatientById(id)
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patient,
            Authentication authentication
    ) {
        try {
            Patient updatedPatient =
                    patientService.updatePatient(id, patient);

            activityService.record(
                    "PATIENT_UPDATED",
                    authentication.getName(),
                    "Patient code: " + updatedPatient.getPatientCode()
            );

            return ResponseEntity.ok(updatedPatient);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}

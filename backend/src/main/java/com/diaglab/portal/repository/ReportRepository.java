package com.diaglab.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diaglab.portal.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByTestId(Long testId);

    List<Report> findByStatusIgnoreCase(String status);
}
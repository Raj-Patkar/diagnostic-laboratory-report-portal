package com.diaglab.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diaglab.portal.entity.ActivityLog;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findAllByOrderByCreatedAtDesc();

    List<ActivityLog> findTop10ByOrderByCreatedAtDesc();
}
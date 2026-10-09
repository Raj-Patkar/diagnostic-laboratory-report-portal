package com.diaglab.portal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.diaglab.portal.entity.ActivityLog;
import com.diaglab.portal.repository.ActivityLogRepository;

@Service
public class ActivityService {

    private final ActivityLogRepository repository;

    public ActivityService(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public ActivityLog record(
            String action,
            String username,
            String details
    ) {
        return repository.save(
                new ActivityLog(action, username, details)
        );
    }

    public List<ActivityLog> getRecentActivity() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<ActivityLog> getAllActivity() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}
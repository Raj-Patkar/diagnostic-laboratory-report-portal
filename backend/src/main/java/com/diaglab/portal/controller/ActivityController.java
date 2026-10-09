package com.diaglab.portal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diaglab.portal.entity.ActivityLog;
import com.diaglab.portal.service.ActivityService;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getActivity() {
        return ResponseEntity.ok(
                activityService.getAllActivity()
        );
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLog>> getRecentActivity() {
        return ResponseEntity.ok(
                activityService.getRecentActivity()
        );
    }
}
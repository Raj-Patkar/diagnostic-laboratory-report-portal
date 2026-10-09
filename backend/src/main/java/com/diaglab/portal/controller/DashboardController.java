package com.diaglab.portal.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diaglab.portal.service.ActivityService;
import com.diaglab.portal.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class DashboardController {

    private final DashboardService dashboardService;
    private final ActivityService activityService;

    public DashboardController(
            DashboardService dashboardService,
            ActivityService activityService
    ) {
        this.dashboardService = dashboardService;
        this.activityService = activityService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("summary", dashboardService.getSummary());
        data.put("recentActivity", activityService.getRecentActivity());

        return ResponseEntity.ok(data);
    }
}
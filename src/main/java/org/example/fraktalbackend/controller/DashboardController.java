package org.example.fraktalbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.dashboard.StudentDashboardResponse;
import org.example.fraktalbackend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/me/dashboard")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getStudentDashboard(authentication.getName()));
    }
}

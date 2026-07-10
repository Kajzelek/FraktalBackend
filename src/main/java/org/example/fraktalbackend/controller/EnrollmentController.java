package org.example.fraktalbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.dto.enrollment.EnrollmentResponse;
import org.example.fraktalbackend.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @GetMapping("/me/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.getMyCourses(authentication.getName()));
    }

    @PostMapping("/admin/users/{userId}/courses/{courseId}/grant-access")
    public ResponseEntity<EnrollmentResponse> grantAccess(
            @PathVariable UUID userId,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(enrollmentService.grantAccess(userId, courseId));
    }
}

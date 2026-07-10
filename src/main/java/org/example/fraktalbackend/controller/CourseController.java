package org.example.fraktalbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.dto.course.CreateCourseRequest;
import org.example.fraktalbackend.dto.course.UpdateCourseRequest;
import org.example.fraktalbackend.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @PostMapping("/admin/courses")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.createCourse(request, authentication.getName()));
    }

    @PutMapping("/admin/courses/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, request));
    }

    @DeleteMapping("/admin/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}

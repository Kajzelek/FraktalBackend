package org.example.fraktalbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.course.CourseAccessResponse;
import org.example.fraktalbackend.dto.course.CourseCatalogResponse;
import org.example.fraktalbackend.dto.course.CourseContentResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
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
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    @GetMapping("/courses/catalog")
    public ResponseEntity<List<CourseCatalogResponse>> getCourseCatalog(Authentication authentication) {
        return ResponseEntity.ok(courseService.getCourseCatalog(authentication.getName()));
    }

    @GetMapping("/admin/courses")
    public ResponseEntity<List<CourseResponse>> getAllCoursesForAdmin() {
        return ResponseEntity.ok(courseService.getAllCoursesForAdmin());
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.getCourseById(courseId, authentication.getName()));
    }

    @GetMapping("/courses/{courseId}/content")
    public ResponseEntity<CourseContentResponse> getCourseContent(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.getCourseContent(courseId, authentication.getName()));
    }

    @GetMapping("/courses/{courseId}/access")
    public ResponseEntity<CourseAccessResponse> getCourseAccess(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.getCourseAccess(courseId, authentication.getName()));
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

    @PatchMapping("/admin/courses/{courseId}/publish")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.publishCourse(courseId));
    }

    @PatchMapping("/admin/courses/{courseId}/unpublish")
    public ResponseEntity<CourseResponse> unpublishCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.unpublishCourse(courseId));
    }
}

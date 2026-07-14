package org.example.fraktalbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.progress.CourseStartResponse;
import org.example.fraktalbackend.dto.progress.ContinueLessonResponse;
import org.example.fraktalbackend.dto.progress.CourseProgressResponse;
import org.example.fraktalbackend.dto.progress.LessonProgressResponse;
import org.example.fraktalbackend.service.LessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonProgressController {
    private final LessonProgressService lessonProgressService;

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<LessonProgressResponse> completeLesson(
            @PathVariable UUID lessonId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonProgressService.completeLesson(lessonId, authentication.getName()));
    }

    @DeleteMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<LessonProgressResponse> uncompleteLesson(
            @PathVariable UUID lessonId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonProgressService.uncompleteLesson(lessonId, authentication.getName()));
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonProgressService.getCourseProgress(courseId, authentication.getName()));
    }

    @GetMapping("/courses/{courseId}/continue")
    public ResponseEntity<ContinueLessonResponse> getContinueLesson(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonProgressService.getContinueLesson(courseId, authentication.getName()));
    }

    @GetMapping("/courses/{courseId}/start")
    public ResponseEntity<CourseStartResponse> getCourseStart(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonProgressService.getCourseStart(courseId, authentication.getName()));
    }
}

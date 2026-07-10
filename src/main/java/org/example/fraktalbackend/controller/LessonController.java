package org.example.fraktalbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.lesson.CreateLessonRequest;
import org.example.fraktalbackend.dto.lesson.LessonPlayerResponse;
import org.example.fraktalbackend.dto.lesson.LessonResponse;
import org.example.fraktalbackend.dto.lesson.UpdateLessonRequest;
import org.example.fraktalbackend.service.LessonService;
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
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/chapters/{chapterId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessonsByChapter(@PathVariable UUID chapterId) {
        return ResponseEntity.ok(lessonService.getLessonsByChapter(chapterId));
    }

    @GetMapping("/lessons/{lessonId}/play")
    public ResponseEntity<LessonPlayerResponse> getLessonPlayerData(
            @PathVariable UUID lessonId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonService.getLessonPlayerData(lessonId, authentication.getName()));
    }

    @PostMapping("/admin/chapters/{chapterId}/lessons")
    public ResponseEntity<LessonResponse> createLesson(
            @PathVariable UUID chapterId,
            @Valid @RequestBody CreateLessonRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lessonService.createLesson(chapterId, request));
    }

    @PutMapping("/admin/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable UUID lessonId,
            @Valid @RequestBody UpdateLessonRequest request
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, request));
    }

    @DeleteMapping("/admin/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}

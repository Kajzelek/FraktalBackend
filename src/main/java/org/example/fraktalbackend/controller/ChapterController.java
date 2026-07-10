package org.example.fraktalbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.chapter.ChapterResponse;
import org.example.fraktalbackend.dto.chapter.CreateChapterRequest;
import org.example.fraktalbackend.dto.chapter.UpdateChapterRequest;
import org.example.fraktalbackend.service.ChapterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping("/courses/{courseId}/chapters")
    public ResponseEntity<List<ChapterResponse>> getChaptersByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(chapterService.getChaptersByCourse(courseId));
    }

    @PostMapping("/admin/courses/{courseId}/chapters")
    public ResponseEntity<ChapterResponse> createChapter(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateChapterRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chapterService.createChapter(courseId, request));
    }

    @PutMapping("/admin/chapters/{chapterId}")
    public ResponseEntity<ChapterResponse> updateChapter(
            @PathVariable UUID chapterId,
            @Valid @RequestBody UpdateChapterRequest request
    ) {
        return ResponseEntity.ok(chapterService.updateChapter(chapterId, request));
    }

    @DeleteMapping("/admin/chapters/{chapterId}")
    public ResponseEntity<Void> deleteChapter(@PathVariable UUID chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }
}

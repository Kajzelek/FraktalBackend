package org.example.fraktalbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.lessonmaterial.CreateLessonMaterialRequest;
import org.example.fraktalbackend.dto.lessonmaterial.LessonMaterialResponse;
import org.example.fraktalbackend.dto.lessonmaterial.UpdateLessonMaterialRequest;
import org.example.fraktalbackend.service.LessonMaterialService;
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
public class LessonMaterialController {
    private final LessonMaterialService lessonMaterialService;

    @GetMapping("/lessons/{lessonId}/materials")
    public ResponseEntity<List<LessonMaterialResponse>> getLessonMaterials(
            @PathVariable UUID lessonId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonMaterialService.getLessonMaterials(lessonId, authentication.getName()));
    }

    @PostMapping("/admin/lessons/{lessonId}/materials")
    public ResponseEntity<LessonMaterialResponse> createLessonMaterial(
            @PathVariable UUID lessonId,
            @Valid @RequestBody CreateLessonMaterialRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lessonMaterialService.createLessonMaterial(lessonId, request));
    }

    @PutMapping("/admin/lesson-materials/{materialId}")
    public ResponseEntity<LessonMaterialResponse> updateLessonMaterial(
            @PathVariable UUID materialId,
            @Valid @RequestBody UpdateLessonMaterialRequest request
    ) {
        return ResponseEntity.ok(lessonMaterialService.updateLessonMaterial(materialId, request));
    }

    @DeleteMapping("/admin/lesson-materials/{materialId}")
    public ResponseEntity<Void> deleteLessonMaterial(@PathVariable UUID materialId) {
        lessonMaterialService.deleteLessonMaterial(materialId);
        return ResponseEntity.noContent().build();
    }
}

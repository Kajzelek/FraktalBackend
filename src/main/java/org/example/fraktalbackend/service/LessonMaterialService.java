package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.lessonmaterial.CreateLessonMaterialRequest;
import org.example.fraktalbackend.dto.lessonmaterial.LessonMaterialResponse;
import org.example.fraktalbackend.dto.lessonmaterial.UpdateLessonMaterialRequest;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Lesson;
import org.example.fraktalbackend.model.LessonMaterial;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.LessonMaterialRepository;
import org.example.fraktalbackend.repository.LessonRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonMaterialService {
    private final LessonMaterialRepository lessonMaterialRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;

    public List<LessonMaterialResponse> getLessonMaterials(UUID lessonId, String userEmail) {
        Lesson lesson = findLessonById(lessonId);
        ensureUserCanAccessLesson(lesson, userEmail);

        return lessonMaterialRepository.findByLessonIdOrderByPositionAsc(lessonId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LessonMaterialResponse createLessonMaterial(UUID lessonId, CreateLessonMaterialRequest request) {
        Lesson lesson = findLessonById(lessonId);

        LessonMaterial material = LessonMaterial.builder()
                .lesson(lesson)
                .title(request.getTitle())
                .type(request.getType())
                .url(request.getUrl())
                .position(request.getPosition())
                .build();

        return toResponse(lessonMaterialRepository.save(material));
    }

    public LessonMaterialResponse updateLessonMaterial(UUID materialId, UpdateLessonMaterialRequest request) {
        LessonMaterial material = findMaterialById(materialId);

        material.setTitle(request.getTitle());
        material.setType(request.getType());
        material.setUrl(request.getUrl());
        material.setPosition(request.getPosition());

        return toResponse(lessonMaterialRepository.save(material));
    }

    public void deleteLessonMaterial(UUID materialId) {
        LessonMaterial material = findMaterialById(materialId);
        lessonMaterialRepository.delete(material);
    }

    private void ensureUserCanAccessLesson(Lesson lesson, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID courseId = lesson.getChapter().getCourse().getId();
        boolean admin = user.getRole() == Role.ROLE_ADMIN;
        boolean hasAccess = enrollmentService.hasAccess(user.getId(), courseId);

        if (!lesson.isFree() && !admin && !hasAccess) {
            throw new AccessDeniedException("You do not have access to this lesson");
        }
    }

    private Lesson findLessonById(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }

    private LessonMaterial findMaterialById(UUID materialId) {
        return lessonMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson material not found"));
    }

    private LessonMaterialResponse toResponse(LessonMaterial material) {
        return new LessonMaterialResponse(
                material.getId(),
                material.getLesson().getId(),
                material.getTitle(),
                material.getType(),
                material.getUrl(),
                material.getPosition()
        );
    }
}

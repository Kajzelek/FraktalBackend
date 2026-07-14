package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.lessonmaterial.CreateLessonMaterialRequest;
import org.example.fraktalbackend.dto.lessonmaterial.LessonMaterialResponse;
import org.example.fraktalbackend.dto.lessonmaterial.SetLessonPdfRequest;
import org.example.fraktalbackend.dto.lessonmaterial.SetLessonVideoRequest;
import org.example.fraktalbackend.dto.lessonmaterial.UpdateLessonMaterialRequest;
import org.example.fraktalbackend.exception.InvalidLessonMaterialException;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Lesson;
import org.example.fraktalbackend.model.LessonMaterial;
import org.example.fraktalbackend.model.LessonMaterialProvider;
import org.example.fraktalbackend.model.LessonMaterialStatus;
import org.example.fraktalbackend.model.LessonMaterialType;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.LessonMaterialRepository;
import org.example.fraktalbackend.repository.LessonRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        LessonMaterialProvider provider = resolveProvider(request.getProvider());
        LessonMaterialStatus status = resolveStatus(request.getStatus());
        validateStorageFields(provider, request.getUrl(), request.getProviderAssetId());

        LessonMaterial material = LessonMaterial.builder()
                .lesson(lesson)
                .title(request.getTitle())
                .type(request.getType())
                .url(request.getUrl())
                .provider(provider)
                .providerAssetId(request.getProviderAssetId())
                .durationSeconds(request.getDurationSeconds())
                .thumbnailUrl(request.getThumbnailUrl())
                .status(status)
                .position(request.getPosition())
                .build();

        return toResponse(lessonMaterialRepository.save(material));
    }

    public LessonMaterialResponse updateLessonMaterial(UUID materialId, UpdateLessonMaterialRequest request) {
        LessonMaterial material = findMaterialById(materialId);
        LessonMaterialProvider provider = resolveProvider(request.getProvider());
        LessonMaterialStatus status = resolveStatus(request.getStatus());
        validateStorageFields(provider, request.getUrl(), request.getProviderAssetId());

        material.setTitle(request.getTitle());
        material.setType(request.getType());
        material.setUrl(request.getUrl());
        material.setProvider(provider);
        material.setProviderAssetId(request.getProviderAssetId());
        material.setDurationSeconds(request.getDurationSeconds());
        material.setThumbnailUrl(request.getThumbnailUrl());
        material.setStatus(status);
        material.setPosition(request.getPosition());

        return toResponse(lessonMaterialRepository.save(material));
    }

    public LessonMaterialResponse upsertLessonVideo(UUID lessonId, SetLessonVideoRequest request) {
        Lesson lesson = findLessonById(lessonId);
        LessonMaterialProvider provider = resolveProvider(request.getProvider());
        LessonMaterialStatus status = resolveStatus(request.getStatus());
        validateStorageFields(provider, request.getUrl(), request.getProviderAssetId());

        LessonMaterial material = lessonMaterialRepository
                .findFirstByLessonIdAndTypeOrderByPositionAsc(lessonId, LessonMaterialType.VIDEO)
                .orElseGet(() -> LessonMaterial.builder()
                        .lesson(lesson)
                        .title("Wideo lekcji")
                        .type(LessonMaterialType.VIDEO)
                        .position(0)
                        .build());

        material.setTitle("Wideo lekcji");
        material.setType(LessonMaterialType.VIDEO);
        material.setUrl(request.getUrl());
        material.setProvider(provider);
        material.setProviderAssetId(request.getProviderAssetId());
        material.setDurationSeconds(request.getDurationSeconds());
        material.setThumbnailUrl(request.getThumbnailUrl());
        material.setStatus(status);
        material.setPosition(0);

        lesson.setVideoUrl(request.getUrl());
        if (request.getDurationSeconds() != null) {
            lesson.setDurationMinutes((int) Math.ceil(request.getDurationSeconds() / 60.0));
        }
        lessonRepository.save(lesson);

        return toResponse(lessonMaterialRepository.save(material));
    }

    public LessonMaterialResponse upsertLessonPdf(UUID lessonId, SetLessonPdfRequest request) {
        Lesson lesson = findLessonById(lessonId);

        LessonMaterial material = lessonMaterialRepository
                .findFirstByLessonIdAndTypeOrderByPositionAsc(lessonId, LessonMaterialType.PDF)
                .orElseGet(() -> LessonMaterial.builder()
                        .lesson(lesson)
                        .type(LessonMaterialType.PDF)
                        .build());

        material.setTitle(resolvePdfTitle(request.getTitle()));
        material.setType(LessonMaterialType.PDF);
        material.setUrl(request.getUrl());
        material.setProvider(LessonMaterialProvider.EXTERNAL_URL);
        material.setProviderAssetId(null);
        material.setDurationSeconds(null);
        material.setThumbnailUrl(null);
        material.setStatus(LessonMaterialStatus.READY);
        material.setPosition(request.getPosition() == null ? 1 : request.getPosition());

        lesson.setPdfUrl(request.getUrl());
        lessonRepository.save(lesson);

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
                material.getProvider(),
                material.getProviderAssetId(),
                material.getDurationSeconds(),
                material.getThumbnailUrl(),
                material.getStatus(),
                material.getPosition()
        );
    }

    private LessonMaterialProvider resolveProvider(LessonMaterialProvider provider) {
        return provider == null ? LessonMaterialProvider.EXTERNAL_URL : provider;
    }

    private LessonMaterialStatus resolveStatus(LessonMaterialStatus status) {
        return status == null ? LessonMaterialStatus.READY : status;
    }

    private String resolvePdfTitle(String title) {
        return StringUtils.hasText(title) ? title : "Notatka PDF";
    }

    private void validateStorageFields(LessonMaterialProvider provider, String url, String providerAssetId) {
        if (provider == LessonMaterialProvider.EXTERNAL_URL && !StringUtils.hasText(url)) {
            throw new InvalidLessonMaterialException("Url is required for EXTERNAL_URL materials");
        }

        if (provider == LessonMaterialProvider.CLOUDFLARE_STREAM && !StringUtils.hasText(providerAssetId)) {
            throw new InvalidLessonMaterialException("Provider asset id is required for CLOUDFLARE_STREAM materials");
        }
    }
}

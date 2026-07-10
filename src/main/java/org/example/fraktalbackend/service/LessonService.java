package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.lesson.CreateLessonRequest;
import org.example.fraktalbackend.dto.lesson.LessonPlayerResponse;
import org.example.fraktalbackend.dto.lesson.LessonResponse;
import org.example.fraktalbackend.dto.lesson.UpdateLessonRequest;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Chapter;
import org.example.fraktalbackend.model.Lesson;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.ChapterRepository;
import org.example.fraktalbackend.repository.LessonRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;

    public LessonResponse createLesson(UUID chapterId, CreateLessonRequest request) {
        Chapter chapter = findChapterById(chapterId);

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .position(request.getPosition())
                .videoUrl(request.getVideoUrl())
                .pdfUrl(request.getPdfUrl())
                .isFree(request.getFree())
                .durationMinutes(request.getDurationMinutes())
                .chapter(chapter)
                .build();

        return mapToResponse(lessonRepository.save(lesson));
    }

    public List<LessonResponse> getLessonsByChapter(UUID chapterId) {
        findChapterById(chapterId);

        return lessonRepository.findByChapterIdOrderByPositionAsc(chapterId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LessonResponse updateLesson(UUID lessonId, UpdateLessonRequest request) {
        Lesson lesson = findLessonById(lessonId);

        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setPosition(request.getPosition());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setPdfUrl(request.getPdfUrl());
        lesson.setFree(request.getFree());
        lesson.setDurationMinutes(request.getDurationMinutes());

        return mapToResponse(lessonRepository.save(lesson));
    }

    public void deleteLesson(UUID lessonId) {
        Lesson lesson = findLessonById(lessonId);
        lessonRepository.delete(lesson);
    }

    public LessonPlayerResponse getLessonPlayerData(UUID lessonId, String userEmail) {
        Lesson lesson = findLessonById(lessonId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID courseId = lesson.getChapter().getCourse().getId();
        boolean admin = user.getRole() == Role.ROLE_ADMIN;
        boolean hasAccess = enrollmentService.hasAccess(user.getId(), courseId);

        if (!lesson.isFree() && !admin && !hasAccess) {
            throw new AccessDeniedException("You do not have access to this lesson");
        }

        return new LessonPlayerResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getVideoUrl(),
                lesson.getPdfUrl(),
                lesson.getDurationMinutes(),
                lesson.isFree(),
                lesson.getChapter().getId(),
                courseId
        );
    }

    private Chapter findChapterById(UUID chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
    }

    private Lesson findLessonById(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getPosition(),
                lesson.getVideoUrl(),
                lesson.getPdfUrl(),
                lesson.isFree(),
                lesson.getDurationMinutes(),
                lesson.getChapter().getId()
        );
    }
}

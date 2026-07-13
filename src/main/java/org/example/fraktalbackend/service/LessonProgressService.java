package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.progress.ContinueLessonResponse;
import org.example.fraktalbackend.dto.progress.CourseProgressResponse;
import org.example.fraktalbackend.dto.progress.LessonProgressResponse;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Lesson;
import org.example.fraktalbackend.model.LessonProgress;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.LessonProgressRepository;
import org.example.fraktalbackend.repository.LessonRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonProgressService {
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;

    public LessonProgressResponse completeLesson(UUID lessonId, String userEmail) {
        Lesson lesson = findLessonById(lessonId);
        User user = findUserByEmail(userEmail);
        UUID courseId = lesson.getChapter().getCourse().getId();

        ensureCanTrackProgress(user, lesson, courseId);

        LessonProgress progress = lessonProgressRepository.findByUserIdAndLessonId(user.getId(), lessonId)
                .orElseGet(() -> LessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .build());

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        LessonProgress savedProgress = lessonProgressRepository.save(progress);
        return new LessonProgressResponse(
                savedProgress.getLesson().getId(),
                savedProgress.isCompleted(),
                savedProgress.getCompletedAt()
        );
    }

    public LessonProgressResponse uncompleteLesson(UUID lessonId, String userEmail) {
        Lesson lesson = findLessonById(lessonId);
        User user = findUserByEmail(userEmail);
        UUID courseId = lesson.getChapter().getCourse().getId();

        ensureCanTrackProgress(user, lesson, courseId);

        LessonProgress progress = lessonProgressRepository.findByUserIdAndLessonId(user.getId(), lessonId)
                .orElse(null);

        if (progress == null) {
            return new LessonProgressResponse(lessonId, false, null);
        }

        progress.setCompleted(false);
        progress.setCompletedAt(null);

        LessonProgress savedProgress = lessonProgressRepository.save(progress);
        return new LessonProgressResponse(
                savedProgress.getLesson().getId(),
                savedProgress.isCompleted(),
                savedProgress.getCompletedAt()
        );
    }

    public CourseProgressResponse getCourseProgress(UUID courseId, String userEmail) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User user = findUserByEmail(userEmail);
        int totalLessons = lessonRepository.countByChapterCourseId(courseId);
        int completedLessons = lessonProgressRepository.countByUserIdAndLessonChapterCourseIdAndCompletedTrue(
                user.getId(),
                courseId
        );
        double progressPercent = totalLessons == 0 ? 0.0 : (completedLessons * 100.0) / totalLessons;

        return new CourseProgressResponse(courseId, totalLessons, completedLessons, progressPercent);
    }

    public ContinueLessonResponse getContinueLesson(UUID courseId, String userEmail) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User user = findUserByEmail(userEmail);
        List<Lesson> lessons = lessonRepository.findByChapterCourseIdOrderByChapterPositionAscPositionAsc(courseId);

        if (lessons.isEmpty()) {
            return new ContinueLessonResponse(
                    courseId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    false,
                    true
            );
        }

        Lesson lessonToContinue = lessons.stream()
                .filter(lesson -> !lessonProgressRepository.existsByUserIdAndLessonIdAndCompletedTrue(
                        user.getId(),
                        lesson.getId()
                ))
                .findFirst()
                .orElse(lessons.get(lessons.size() - 1));

        boolean courseCompleted = lessons.stream()
                .allMatch(lesson -> lessonProgressRepository.existsByUserIdAndLessonIdAndCompletedTrue(
                        user.getId(),
                        lesson.getId()
                ));
        boolean hasAccess = enrollmentService.hasAccess(user.getId(), courseId);
        boolean locked = !lessonToContinue.isFree() && !hasAccess;

        return new ContinueLessonResponse(
                courseId,
                lessonToContinue.getChapter().getId(),
                lessonToContinue.getChapter().getTitle(),
                lessonToContinue.getId(),
                lessonToContinue.getTitle(),
                lessonToContinue.getPosition(),
                lessonToContinue.isFree(),
                locked,
                courseCompleted
        );
    }

    private void ensureCanTrackProgress(User user, Lesson lesson, UUID courseId) {
        boolean admin = user.getRole() == Role.ROLE_ADMIN;
        boolean hasAccess = enrollmentService.hasAccess(user.getId(), courseId);

        if (admin) {
            throw new AccessDeniedException("Admin users do not track lesson progress");
        }

        if (!lesson.isFree() && !hasAccess) {
            throw new AccessDeniedException("You do not have access to this lesson");
        }
    }

    private Lesson findLessonById(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}

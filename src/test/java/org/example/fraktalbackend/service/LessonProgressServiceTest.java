package org.example.fraktalbackend.service;

import org.example.fraktalbackend.model.Chapter;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.model.Lesson;
import org.example.fraktalbackend.model.LessonProgress;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.LessonProgressRepository;
import org.example.fraktalbackend.repository.LessonRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonProgressServiceTest {
    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private LessonProgressService lessonProgressService;

    @Test
    void completeLessonMarksFreeLessonAsCompleted() {
        UUID lessonId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Lesson lesson = createLesson(lessonId, courseId, true);
        User user = createStudent(userId);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(enrollmentService.hasAccess(userId, courseId)).thenReturn(false);
        when(lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId)).thenReturn(Optional.empty());
        when(lessonProgressRepository.save(any(LessonProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = lessonProgressService.completeLesson(lessonId, "student@test.pl");

        ArgumentCaptor<LessonProgress> progressCaptor = ArgumentCaptor.forClass(LessonProgress.class);
        verify(lessonProgressRepository).save(progressCaptor.capture());

        LessonProgress savedProgress = progressCaptor.getValue();
        assertThat(savedProgress.getUser()).isEqualTo(user);
        assertThat(savedProgress.getLesson()).isEqualTo(lesson);
        assertThat(savedProgress.isCompleted()).isTrue();
        assertThat(savedProgress.getCompletedAt()).isNotNull();
        assertThat(response.getLessonId()).isEqualTo(lessonId);
        assertThat(response.isCompleted()).isTrue();
        assertThat(response.getCompletedAt()).isNotNull();
    }

    @Test
    void completeLessonThrowsAccessDeniedForPaidLessonWithoutAccess() {
        UUID lessonId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Lesson lesson = createLesson(lessonId, courseId, false);
        User user = createStudent(userId);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(enrollmentService.hasAccess(userId, courseId)).thenReturn(false);

        assertThatThrownBy(() -> lessonProgressService.completeLesson(lessonId, "student@test.pl"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have access to this lesson");

        verify(lessonProgressRepository, never()).save(any(LessonProgress.class));
    }

    @Test
    void getCourseProgressCalculatesPercent() {
        UUID courseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Course course = Course.builder()
                .id(courseId)
                .build();
        User user = createStudent(userId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(lessonRepository.countByChapterCourseId(courseId)).thenReturn(4);
        when(lessonProgressRepository.countByUserIdAndLessonChapterCourseIdAndCompletedTrue(userId, courseId))
                .thenReturn(1);

        var response = lessonProgressService.getCourseProgress(courseId, "student@test.pl");

        assertThat(response.getCourseId()).isEqualTo(courseId);
        assertThat(response.getTotalLessons()).isEqualTo(4);
        assertThat(response.getCompletedLessons()).isEqualTo(1);
        assertThat(response.getProgressPercent()).isEqualTo(25.0);
    }

    @Test
    void uncompleteLessonMarksProgressAsNotCompleted() {
        UUID lessonId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Lesson lesson = createLesson(lessonId, courseId, true);
        User user = createStudent(userId);
        LessonProgress existingProgress = LessonProgress.builder()
                .user(user)
                .lesson(lesson)
                .completed(true)
                .completedAt(LocalDateTime.now())
                .build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(enrollmentService.hasAccess(userId, courseId)).thenReturn(false);
        when(lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId))
                .thenReturn(Optional.of(existingProgress));
        when(lessonProgressRepository.save(any(LessonProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = lessonProgressService.uncompleteLesson(lessonId, "student@test.pl");

        ArgumentCaptor<LessonProgress> progressCaptor = ArgumentCaptor.forClass(LessonProgress.class);
        verify(lessonProgressRepository).save(progressCaptor.capture());

        LessonProgress savedProgress = progressCaptor.getValue();
        assertThat(savedProgress.isCompleted()).isFalse();
        assertThat(savedProgress.getCompletedAt()).isNull();
        assertThat(response.getLessonId()).isEqualTo(lessonId);
        assertThat(response.isCompleted()).isFalse();
        assertThat(response.getCompletedAt()).isNull();
    }

    private Lesson createLesson(UUID lessonId, UUID courseId, boolean free) {
        Course course = Course.builder()
                .id(courseId)
                .build();
        Chapter chapter = Chapter.builder()
                .id(UUID.randomUUID())
                .course(course)
                .build();

        return Lesson.builder()
                .id(lessonId)
                .title("Lesson")
                .isFree(free)
                .chapter(chapter)
                .build();
    }

    private User createStudent(UUID userId) {
        return User.builder()
                .id(userId)
                .email("student@test.pl")
                .role(Role.ROLE_STUDENT)
                .build();
    }
}

package org.example.fraktalbackend.repository;

import org.example.fraktalbackend.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    Optional<LessonProgress> findByUserIdAndLessonId(UUID userId, UUID lessonId);

    int countByUserIdAndLessonChapterCourseIdAndCompletedTrue(UUID userId, UUID courseId);
}

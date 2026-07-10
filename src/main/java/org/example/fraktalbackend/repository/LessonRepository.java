package org.example.fraktalbackend.repository;

import org.example.fraktalbackend.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByChapterIdOrderByPositionAsc(UUID chapterId);
}

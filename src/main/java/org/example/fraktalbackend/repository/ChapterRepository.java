package org.example.fraktalbackend.repository;

import org.example.fraktalbackend.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    List<Chapter> findByCourseIdOrderByPositionAsc(UUID courseId);
}

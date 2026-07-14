package org.example.fraktalbackend.repository;

import org.example.fraktalbackend.model.LessonMaterial;
import org.example.fraktalbackend.model.LessonMaterialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonMaterialRepository extends JpaRepository<LessonMaterial, UUID> {
    List<LessonMaterial> findByLessonIdOrderByPositionAsc(UUID lessonId);
    Optional<LessonMaterial> findFirstByLessonIdAndTypeOrderByPositionAsc(UUID lessonId, LessonMaterialType type);
}

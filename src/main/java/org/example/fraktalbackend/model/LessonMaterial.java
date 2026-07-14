package org.example.fraktalbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "lesson_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Enumerated(EnumType.STRING)
    private LessonMaterialType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LessonMaterialProvider provider = LessonMaterialProvider.EXTERNAL_URL;

    private String providerAssetId;
    private String url;
    private Integer durationSeconds;
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LessonMaterialStatus status = LessonMaterialStatus.READY;

    private Integer position;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}

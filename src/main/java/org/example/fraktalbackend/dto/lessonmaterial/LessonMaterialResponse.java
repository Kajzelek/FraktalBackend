package org.example.fraktalbackend.dto.lessonmaterial;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.model.LessonMaterialProvider;
import org.example.fraktalbackend.model.LessonMaterialStatus;
import org.example.fraktalbackend.model.LessonMaterialType;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LessonMaterialResponse {
    private UUID id;
    private UUID lessonId;
    private String title;
    private LessonMaterialType type;
    private String url;
    private LessonMaterialProvider provider;
    private String providerAssetId;
    private Integer durationSeconds;
    private String thumbnailUrl;
    private LessonMaterialStatus status;
    private Integer position;
}

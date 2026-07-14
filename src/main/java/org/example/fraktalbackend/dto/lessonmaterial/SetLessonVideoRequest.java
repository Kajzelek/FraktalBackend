package org.example.fraktalbackend.dto.lessonmaterial;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.example.fraktalbackend.model.LessonMaterialProvider;
import org.example.fraktalbackend.model.LessonMaterialStatus;

@Data
public class SetLessonVideoRequest {
    private LessonMaterialProvider provider;
    private String url;
    private String providerAssetId;

    @PositiveOrZero(message = "Duration seconds cannot be negative")
    private Integer durationSeconds;

    private String thumbnailUrl;
    private LessonMaterialStatus status;
}

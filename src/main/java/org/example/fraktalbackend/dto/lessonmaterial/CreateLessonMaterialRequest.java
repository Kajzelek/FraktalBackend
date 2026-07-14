package org.example.fraktalbackend.dto.lessonmaterial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.example.fraktalbackend.model.LessonMaterialProvider;
import org.example.fraktalbackend.model.LessonMaterialStatus;
import org.example.fraktalbackend.model.LessonMaterialType;

@Data
public class CreateLessonMaterialRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Material type is required")
    private LessonMaterialType type;

    private String url;

    private LessonMaterialProvider provider;
    private String providerAssetId;

    @PositiveOrZero(message = "Duration seconds cannot be negative")
    private Integer durationSeconds;

    private String thumbnailUrl;
    private LessonMaterialStatus status;

    @NotNull(message = "Position is required")
    @PositiveOrZero(message = "Position cannot be negative")
    private Integer position;
}

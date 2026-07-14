package org.example.fraktalbackend.dto.lessonmaterial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.example.fraktalbackend.model.LessonMaterialType;

@Data
public class UpdateLessonMaterialRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Material type is required")
    private LessonMaterialType type;

    @NotBlank(message = "Url is required")
    private String url;

    @NotNull(message = "Position is required")
    @PositiveOrZero(message = "Position cannot be negative")
    private Integer position;
}

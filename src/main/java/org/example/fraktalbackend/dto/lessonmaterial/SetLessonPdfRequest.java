package org.example.fraktalbackend.dto.lessonmaterial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SetLessonPdfRequest {
    private String title;

    @NotBlank(message = "Url is required")
    private String url;

    @PositiveOrZero(message = "Position cannot be negative")
    private Integer position;
}

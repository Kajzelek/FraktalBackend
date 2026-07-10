package org.example.fraktalbackend.dto.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateLessonRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Position is required")
    @PositiveOrZero(message = "Position cannot be negative")
    private Integer position;

    private String videoUrl;
    private String pdfUrl;

    @NotNull(message = "Free flag is required")
    private Boolean free;

    @PositiveOrZero(message = "Duration cannot be negative")
    private Integer durationMinutes;
}

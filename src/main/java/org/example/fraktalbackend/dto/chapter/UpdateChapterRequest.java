package org.example.fraktalbackend.dto.chapter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateChapterRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Position is required")
    @PositiveOrZero(message = "Position cannot be negative")
    private Integer position;
}

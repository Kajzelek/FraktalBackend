package org.example.fraktalbackend.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateCourseRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private String thumbnailUrl;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    private Double price;
}

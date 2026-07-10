package org.example.fraktalbackend.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CourseResponse {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Double price;
    private LocalDateTime createdAt;
}

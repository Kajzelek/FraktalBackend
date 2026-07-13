package org.example.fraktalbackend.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LessonContentResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer position;
    private boolean free;
    private boolean locked;
    private Integer durationMinutes;
}

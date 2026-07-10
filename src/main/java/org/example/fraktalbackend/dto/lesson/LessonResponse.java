package org.example.fraktalbackend.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LessonResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer position;
    private String videoUrl;
    private String pdfUrl;
    private boolean free;
    private Integer durationMinutes;
    private UUID chapterId;
}

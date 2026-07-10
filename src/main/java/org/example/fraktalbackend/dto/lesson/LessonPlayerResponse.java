package org.example.fraktalbackend.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LessonPlayerResponse {
    private UUID lessonId;
    private String title;
    private String description;
    private String videoUrl;
    private String pdfUrl;
    private Integer durationMinutes;
    private boolean free;
    private UUID chapterId;
    private UUID courseId;
}

package org.example.fraktalbackend.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.dto.lessonmaterial.LessonMaterialResponse;

import java.time.LocalDateTime;
import java.util.List;
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
    private UUID previousLessonId;
    private UUID nextLessonId;
    private boolean completed;
    private LocalDateTime completedAt;
    private LessonMaterialResponse primaryVideo;
    private LessonMaterialResponse primaryPdf;
    private List<LessonMaterialResponse> materials;
}

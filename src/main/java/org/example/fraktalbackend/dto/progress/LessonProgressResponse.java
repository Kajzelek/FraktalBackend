package org.example.fraktalbackend.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LessonProgressResponse {
    private UUID lessonId;
    private boolean completed;
    private LocalDateTime completedAt;
}

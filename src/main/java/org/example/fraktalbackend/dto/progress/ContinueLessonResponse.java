package org.example.fraktalbackend.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ContinueLessonResponse {
    private UUID courseId;
    private UUID chapterId;
    private String chapterTitle;
    private UUID lessonId;
    private String lessonTitle;
    private Integer lessonPosition;
    private boolean free;
    private boolean locked;
    private boolean courseCompleted;
}

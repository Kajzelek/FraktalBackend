package org.example.fraktalbackend.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CourseStartResponse {
    private UUID courseId;
    private UUID chapterId;
    private String chapterTitle;
    private UUID lessonId;
    private String lessonTitle;
    private String mode;
}

package org.example.fraktalbackend.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CourseProgressResponse {
    private UUID courseId;
    private int totalLessons;
    private int completedLessons;
    private double progressPercent;
}

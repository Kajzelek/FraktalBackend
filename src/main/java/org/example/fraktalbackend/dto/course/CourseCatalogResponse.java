package org.example.fraktalbackend.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CourseCatalogResponse {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Double price;
    private boolean hasAccess;
    private int lessonsCount;
    private int completedLessons;
    private double progressPercent;
    private boolean freePreviewAvailable;
    private boolean canStart;
}

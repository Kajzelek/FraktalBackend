package org.example.fraktalbackend.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CourseContentResponse {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Double price;
    private boolean published;
    private boolean hasAccess;
    private List<ChapterContentResponse> chapters;
}

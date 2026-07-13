package org.example.fraktalbackend.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ChapterContentResponse {
    private UUID id;
    private String title;
    private Integer position;
    private List<LessonContentResponse> lessons;
}

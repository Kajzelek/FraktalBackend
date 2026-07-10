package org.example.fraktalbackend.dto.chapter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChapterResponse {
    private UUID id;
    private String title;
    private Integer position;
    private UUID courseId;
}

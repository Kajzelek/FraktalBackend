package org.example.fraktalbackend.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CourseAccessResponse {
    private UUID courseId;
    private boolean published;
    private boolean hasAccess;
    private boolean admin;
    private boolean canViewContent;
    private boolean canStart;
    private boolean freePreviewAvailable;
}

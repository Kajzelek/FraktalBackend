package org.example.fraktalbackend.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.dto.course.CourseResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UUID userId;
    private CourseResponse course;
    private LocalDateTime enrollmentDate;
}

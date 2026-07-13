package org.example.fraktalbackend.mapper;

import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getThumbnailUrl(),
                course.getPrice(),
                course.isPublished(),
                course.getCreatedAt()
        );
    }
}

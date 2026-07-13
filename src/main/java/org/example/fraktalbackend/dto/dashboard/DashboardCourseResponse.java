package org.example.fraktalbackend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.dto.progress.ContinueLessonResponse;
import org.example.fraktalbackend.dto.progress.CourseProgressResponse;

@Data
@AllArgsConstructor
public class DashboardCourseResponse {
    private CourseResponse course;
    private CourseProgressResponse progress;
    private ContinueLessonResponse continueLesson;
}

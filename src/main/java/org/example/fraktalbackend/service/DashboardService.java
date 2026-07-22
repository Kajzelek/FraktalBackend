package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.dto.dashboard.DashboardCourseResponse;
import org.example.fraktalbackend.dto.dashboard.StudentDashboardResponse;
import org.example.fraktalbackend.dto.user.UserProfileResponse;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;
    private final LessonProgressService lessonProgressService;

    public StudentDashboardResponse getStudentDashboard(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfileResponse userProfile = new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getNickname(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );

        List<DashboardCourseResponse> courses = enrollmentService.getMyCourses(userEmail)
                .stream()
                .map(course -> mapToDashboardCourse(course, userEmail))
                .toList();

        return new StudentDashboardResponse(userProfile, courses);
    }

    private DashboardCourseResponse mapToDashboardCourse(CourseResponse course, String userEmail) {
        return new DashboardCourseResponse(
                course,
                lessonProgressService.getCourseProgress(course.getId(), userEmail),
                lessonProgressService.getContinueLesson(course.getId(), userEmail)
        );
    }
}

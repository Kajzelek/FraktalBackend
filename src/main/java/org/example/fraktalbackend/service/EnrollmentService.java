package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.dto.enrollment.EnrollmentResponse;
import org.example.fraktalbackend.exception.EnrollmentAlreadyExistsException;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.model.Enrollment;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.EnrollmentRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentResponse grantAccess(UUID userId, UUID courseId) {
        User user = findUserById(userId);
        Course course = findCourseById(courseId);

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new EnrollmentAlreadyExistsException("User already has access to this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .build();

        return mapToEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    public List<CourseResponse> getMyCourses(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return enrollmentRepository.findByUserId(user.getId())
                .stream()
                .map(Enrollment::getCourse)
                .map(this::mapToCourseResponse)
                .toList();
    }

    public boolean hasAccess(UUID userId, UUID courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Course findCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getUser().getId(),
                mapToCourseResponse(enrollment.getCourse()),
                enrollment.getEnrollmentDate()
        );
    }

    private CourseResponse mapToCourseResponse(Course course) {
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

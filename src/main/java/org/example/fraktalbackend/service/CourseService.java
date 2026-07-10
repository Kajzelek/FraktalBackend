package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.dto.course.CreateCourseRequest;
import org.example.fraktalbackend.dto.course.UpdateCourseRequest;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseResponse createCourse(CreateCourseRequest request, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));


        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .thumbnailUrl(request.getThumbnailUrl())
                .price(request.getPrice())
                .instructor(instructor)
                .build();

        return mapToResponse(courseRepository.save(course));
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CourseResponse getCourseById(UUID courseId) {
        Course course = findCourseById(courseId);
        return mapToResponse(course);
    }

    public CourseResponse updateCourse(UUID courseId, UpdateCourseRequest request) {
        Course course = findCourseById(courseId);

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPrice(request.getPrice());

        return mapToResponse(courseRepository.save(course));
    }

    public void deleteCourse(UUID courseId) {
        Course course = findCourseById(courseId);
        courseRepository.delete(course);
    }

    private Course findCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private CourseResponse mapToResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getThumbnailUrl(),
                course.getPrice(),
                course.getCreatedAt()
        );
    }
}

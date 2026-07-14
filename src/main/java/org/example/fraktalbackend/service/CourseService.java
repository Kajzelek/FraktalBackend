package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.course.ChapterContentResponse;
import org.example.fraktalbackend.dto.course.CourseAccessResponse;
import org.example.fraktalbackend.dto.course.CourseContentResponse;
import org.example.fraktalbackend.dto.course.CourseResponse;
import org.example.fraktalbackend.dto.course.CreateCourseRequest;
import org.example.fraktalbackend.dto.course.LessonContentResponse;
import org.example.fraktalbackend.dto.course.UpdateCourseRequest;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Chapter;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.model.Lesson;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.mapper.CourseMapper;
import org.example.fraktalbackend.repository.ChapterRepository;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.LessonRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentService enrollmentService;
    private final CourseMapper courseMapper;

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

        return courseMapper.toResponse(courseRepository.save(course));
    }

    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByPublishedTrue()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    public List<CourseResponse> getAllCoursesForAdmin() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    public CourseResponse getCourseById(UUID courseId, String userEmail) {
        Course course = findCourseById(courseId);
        ensureCourseVisibleToUser(course, userEmail);
        return courseMapper.toResponse(course);
    }

    public CourseContentResponse getCourseContent(UUID courseId, String userEmail) {
        Course course = findCourseById(courseId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean admin = user.getRole() == Role.ROLE_ADMIN;
        if (!course.isPublished() && !admin) {
            throw new AccessDeniedException("Course is not published");
        }

        boolean hasAccess = admin || enrollmentService.hasAccess(user.getId(), courseId);

        List<ChapterContentResponse> chapters = chapterRepository.findByCourseIdOrderByPositionAsc(courseId)
                .stream()
                .map(chapter -> mapToChapterContentResponse(chapter, hasAccess))
                .toList();

        return new CourseContentResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getThumbnailUrl(),
                course.getPrice(),
                course.isPublished(),
                hasAccess,
                chapters
        );
    }

    public CourseAccessResponse getCourseAccess(UUID courseId, String userEmail) {
        Course course = findCourseById(courseId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean admin = user.getRole() == Role.ROLE_ADMIN;
        boolean hasEnrollment = enrollmentService.hasAccess(user.getId(), courseId);
        boolean freePreviewAvailable = lessonRepository.existsByChapterCourseIdAndIsFreeTrue(courseId);
        boolean canViewContent = course.isPublished() || admin;
        boolean canStart = canViewContent && (admin || hasEnrollment || freePreviewAvailable);

        return new CourseAccessResponse(
                course.getId(),
                course.isPublished(),
                hasEnrollment,
                admin,
                canViewContent,
                canStart,
                freePreviewAvailable
        );
    }

    public CourseResponse updateCourse(UUID courseId, UpdateCourseRequest request) {
        Course course = findCourseById(courseId);

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPrice(request.getPrice());

        return courseMapper.toResponse(courseRepository.save(course));
    }

    public void deleteCourse(UUID courseId) {
        Course course = findCourseById(courseId);
        courseRepository.delete(course);
    }

    public CourseResponse publishCourse(UUID courseId) {
        Course course = findCourseById(courseId);
        course.setPublished(true);
        return courseMapper.toResponse(courseRepository.save(course));
    }

    public CourseResponse unpublishCourse(UUID courseId) {
        Course course = findCourseById(courseId);
        course.setPublished(false);
        return courseMapper.toResponse(courseRepository.save(course));
    }

    private Course findCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private void ensureCourseVisibleToUser(Course course, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!course.isPublished() && user.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("Course is not published");
        }
    }

    private ChapterContentResponse mapToChapterContentResponse(Chapter chapter, boolean hasAccess) {
        List<LessonContentResponse> lessons = lessonRepository.findByChapterIdOrderByPositionAsc(chapter.getId())
                .stream()
                .map(lesson -> mapToLessonContentResponse(lesson, hasAccess))
                .toList();

        return new ChapterContentResponse(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getPosition(),
                lessons
        );
    }

    private LessonContentResponse mapToLessonContentResponse(Lesson lesson, boolean hasAccess) {
        boolean locked = !lesson.isFree() && !hasAccess;

        return new LessonContentResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getPosition(),
                lesson.isFree(),
                locked,
                lesson.getDurationMinutes()
        );
    }
}

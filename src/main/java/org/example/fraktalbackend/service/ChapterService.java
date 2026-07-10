package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.chapter.ChapterResponse;
import org.example.fraktalbackend.dto.chapter.CreateChapterRequest;
import org.example.fraktalbackend.dto.chapter.UpdateChapterRequest;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.model.Chapter;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.repository.ChapterRepository;
import org.example.fraktalbackend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    public ChapterResponse createChapter(UUID courseId, CreateChapterRequest request) {
        Course course = findCourseById(courseId);

        Chapter chapter = Chapter.builder()
                .title(request.getTitle())
                .position(request.getPosition())
                .course(course)
                .build();

        return mapToResponse(chapterRepository.save(chapter));
    }

    public List<ChapterResponse> getChaptersByCourse(UUID courseId) {
        findCourseById(courseId);

        return chapterRepository.findByCourseIdOrderByPositionAsc(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ChapterResponse updateChapter(UUID chapterId, UpdateChapterRequest request) {
        Chapter chapter = findChapterById(chapterId);

        chapter.setTitle(request.getTitle());
        chapter.setPosition(request.getPosition());

        return mapToResponse(chapterRepository.save(chapter));
    }

    public void deleteChapter(UUID chapterId) {
        Chapter chapter = findChapterById(chapterId);
        chapterRepository.delete(chapter);
    }

    private Course findCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private Chapter findChapterById(UUID chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
    }

    private ChapterResponse mapToResponse(Chapter chapter) {
        return new ChapterResponse(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getPosition(),
                chapter.getCourse().getId()
        );
    }
}

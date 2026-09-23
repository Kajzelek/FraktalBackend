package org.example.fraktalbackend.config;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.model.Chapter;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.model.Enrollment;
import org.example.fraktalbackend.model.Lesson;
import org.example.fraktalbackend.model.LessonMaterial;
import org.example.fraktalbackend.model.LessonMaterialProvider;
import org.example.fraktalbackend.model.LessonMaterialStatus;
import org.example.fraktalbackend.model.LessonMaterialType;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.ChapterRepository;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.EnrollmentRepository;
import org.example.fraktalbackend.repository.LessonMaterialRepository;
import org.example.fraktalbackend.repository.LessonRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {
    private static final String ADMIN_EMAIL = "admin@fraktal.pl";
    private static final String STUDENT_EMAIL = "student@fraktal.pl";
    private static final String STUDENT_EMAIL2 = "student@fraktal2.pl";
    private static final String STUDENT_EMAIL3 = "student@fraktal3.pl";
    private static final String COURSE_TITLE = "Matura podstawowa - kurs testowy";

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final LessonMaterialRepository lessonMaterialRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User admin = createUserIfMissing(
                ADMIN_EMAIL,
                "Admin123!",
                "Admin",
                "Fraktal",
                "admin",
                Role.ROLE_ADMIN
        );
        User student = createUserIfMissing(
                STUDENT_EMAIL,
                "Student123!",
                "Student",
                "Testowy",
                "student",
                Role.ROLE_STUDENT
        );

        User student2 = createUserIfMissing(
                STUDENT_EMAIL2,
                "Student123!",
                "Student2",
                "Testowy2",
                "student2",
                Role.ROLE_STUDENT
        );

        User student3 = createUserIfMissing(
                STUDENT_EMAIL3,
                "Student123!",
                "Student3",
                "Testowy3",
                "student3",
                Role.ROLE_STUDENT
        );

        Course course = createCourseIfMissing(admin);
        grantAccessIfMissing(student, course);
    }

    private User createUserIfMissing(
            String email,
            String rawPassword,
            String firstName,
            String lastName,
            String nickname,
            Role role
    ) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .nickname(nickname)
                        .username(email)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(role)
                        .createdAt(LocalDateTime.now())
                        .build()));
    }

    private Course createCourseIfMissing(User admin) {
        return courseRepository.findByTitle(COURSE_TITLE)
                .orElseGet(() -> {
                    Course course = courseRepository.save(Course.builder()
                            .title(COURSE_TITLE)
                            .description("Przykładowy kurs do testowania listy kursów, checkoutu i playera lekcji.")
                            .category("Matura podstawowa")
                            .thumbnailUrl("https://placehold.co/1200x675?text=Matura+podstawowa")
                            .price(199.0)
                            .published(true)
                            .instructor(admin)
                            .createdAt(LocalDateTime.now())
                            .build());

                    createCourseStructure(course);
                    return course;
                });
    }

    private void createCourseStructure(Course course) {
        Chapter functions = chapterRepository.save(Chapter.builder()
                .course(course)
                .title("Funkcje")
                .position(0)
                .build());
        Chapter equations = chapterRepository.save(Chapter.builder()
                .course(course)
                .title("Równania i nierówności")
                .position(1)
                .build());

        Lesson intro = createLesson(
                functions,
                "Wprowadzenie do funkcji",
                "Czym jest funkcja i jak czytać jej wykres.",
                0,
                true,
                18,
                "https://example.com/videos/wprowadzenie-do-funkcji.mp4",
                "https://example.com/pdf/funkcje-wprowadzenie.pdf"
        );
        Lesson linear = createLesson(
                functions,
                "Funkcja liniowa",
                "Najważniejsze własności funkcji liniowej i przykłady maturalne.",
                1,
                false,
                32,
                "https://example.com/videos/funkcja-liniowa.mp4",
                "https://example.com/pdf/funkcja-liniowa.pdf"
        );
        Lesson quadratic = createLesson(
                functions,
                "Funkcja kwadratowa",
                "Postacie funkcji kwadratowej, miejsca zerowe i wierzchołek paraboli.",
                2,
                false,
                41,
                "https://example.com/videos/funkcja-kwadratowa.mp4",
                "https://example.com/pdf/funkcja-kwadratowa.pdf"
        );
        Lesson equationsIntro = createLesson(
                equations,
                "Równania liniowe",
                "Rozwiązywanie równań liniowych krok po kroku.",
                0,
                false,
                26,
                "https://example.com/videos/rownania-liniowe.mp4",
                "https://example.com/pdf/rownania-liniowe.pdf"
        );

        createMaterials(intro);
        createMaterials(linear);
        createMaterials(quadratic);
        createMaterials(equationsIntro);
    }

    private Lesson createLesson(
            Chapter chapter,
            String title,
            String description,
            int position,
            boolean free,
            int durationMinutes,
            String videoUrl,
            String pdfUrl
    ) {
        return lessonRepository.save(Lesson.builder()
                .chapter(chapter)
                .title(title)
                .description(description)
                .position(position)
                .isFree(free)
                .durationMinutes(durationMinutes)
                .videoUrl(videoUrl)
                .pdfUrl(pdfUrl)
                .build());
    }

    private void createMaterials(Lesson lesson) {
        lessonMaterialRepository.save(LessonMaterial.builder()
                .lesson(lesson)
                .title("Wideo lekcji")
                .type(LessonMaterialType.VIDEO)
                .url(lesson.getVideoUrl())
                .provider(LessonMaterialProvider.EXTERNAL_URL)
                .durationSeconds(lesson.getDurationMinutes() == null ? null : lesson.getDurationMinutes() * 60)
                .thumbnailUrl(lesson.getChapter().getCourse().getThumbnailUrl())
                .status(LessonMaterialStatus.READY)
                .position(0)
                .build());
        lessonMaterialRepository.save(LessonMaterial.builder()
                .lesson(lesson)
                .title("Notatka PDF")
                .type(LessonMaterialType.PDF)
                .url(lesson.getPdfUrl())
                .position(1)
                .build());
        lessonMaterialRepository.save(LessonMaterial.builder()
                .lesson(lesson)
                .title("Dodatkowe przykłady")
                .type(LessonMaterialType.LINK)
                .url("https://example.com/materialy/" + lesson.getId())
                .position(2)
                .build());
    }

    private void grantAccessIfMissing(User student, Course course) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(student.getId(), course.getId())) {
            enrollmentRepository.save(Enrollment.builder()
                    .user(student)
                    .course(course)
                    .enrollmentDate(LocalDateTime.now())
                    .build());
        }
    }
}

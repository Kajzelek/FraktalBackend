package org.example.fraktalbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fraktalbackend.model.Course;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String videoUrl; //S3 link
    private boolean isFree;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}

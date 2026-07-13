package org.example.fraktalbackend.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Double price;
    @Builder.Default
    private boolean published = false;


    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Chapter> chapters;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

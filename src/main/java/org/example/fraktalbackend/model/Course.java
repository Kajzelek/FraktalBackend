package org.example.fraktalbackend.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
import org.example.fraktalbackend.model.User;


@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}

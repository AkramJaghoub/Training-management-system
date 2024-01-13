package ju.example.training_management_system.model;

import jakarta.persistence.*;
import ju.example.training_management_system.dto.FeedbackDto;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import lombok.*;

import java.time.LocalDateTime;

import static ju.example.training_management_system.model.PostStatus.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;
    @ManyToOne
    private Company company;
    @Column(columnDefinition = "LONGTEXT")
    private String comment;
    private double rating;
    private LocalDateTime postDate;
    private PostStatus status;

    @Transient
    private String decompressedImageBase64;

    public Feedback toEntity(FeedbackDto feedbackDto) {
        return Feedback.builder()
                .comment(feedbackDto.getComment())
                .rating(feedbackDto.getRating())
                .postDate(LocalDateTime.now())
                .status(PENDING)
                .build();
    }
}

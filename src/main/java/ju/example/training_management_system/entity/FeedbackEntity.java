package ju.example.training_management_system.entity;

import static ju.example.training_management_system.model.PostStatus.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.model.Feedback;
import ju.example.training_management_system.model.PostStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne private StudentEntity student;
  @ManyToOne private CompanyEntity company;

  @Column(columnDefinition = "LONGTEXT")
  private String comment;

  private double rating;
  @CreatedDate private LocalDateTime postDate;
  private PostStatus status;

  public FeedbackEntity toEntity(Feedback feedback) {
    return FeedbackEntity.builder()
        .comment(feedback.getComment())
        .rating(feedback.getRating())
        .status(PENDING)
        .build();
  }
}

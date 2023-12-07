package ju.example.training_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ju.example.training_management_system.model.users.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PasswordResetToken")
@Getter
@Setter
public class PasswordResetToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String token;
  private LocalDateTime expiryDateTime;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}

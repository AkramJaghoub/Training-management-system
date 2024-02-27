package ju.example.training_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ju.example.training_management_system.entity.users.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PasswordResetTokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;
  private LocalDateTime expiryDateTime;

  @ManyToOne private UserEntity user;
}

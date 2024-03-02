package ju.example.training_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.model.TokenStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TWO_FACTOR_AUTHENTICATION")
public class TwoFactorAuthenticationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;
  private LocalDateTime persistenceTime;
  private LocalDateTime expiryTime;

  @Enumerated(EnumType.STRING)
  private TokenStatus tokenExpiryStatus;

  @ManyToOne private UserEntity user;
}

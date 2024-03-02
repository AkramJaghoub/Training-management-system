package ju.example.training_management_system.entity;

import jakarta.persistence.*;
import ju.example.training_management_system.entity.users.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTIFICATION")
public class NotificationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String message;
  @ManyToOne private UserEntity user;
}

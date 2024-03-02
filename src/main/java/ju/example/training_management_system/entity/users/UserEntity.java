package ju.example.training_management_system.entity.users;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ju.example.training_management_system.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "USER")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;
  @CreatedDate private LocalDateTime joinDate;

  @Enumerated(EnumType.STRING)
  public Role role;

  @Column(columnDefinition = "TEXT")
  private String imageUrl;

  public UserEntity(String email, String password) {
    this.email = email;
    this.password = password;
  }
}

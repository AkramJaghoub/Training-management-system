package ju.example.training_management_system.model.users;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

  @Enumerated(EnumType.STRING)
  public Role role;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;
  private LocalDateTime joinDate;

  @Column(columnDefinition = "TEXT")
  private String imageUrl;

  public User(String email, String password) {
    this.email = email;
    this.password = password;
  }
}

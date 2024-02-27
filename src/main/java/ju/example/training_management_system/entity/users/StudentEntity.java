package ju.example.training_management_system.entity.users;

import static ju.example.training_management_system.util.Utils.capitalizeFirstLetter;

import jakarta.persistence.Entity;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class StudentEntity extends UserEntity {

  private String firstName;
  private String lastName;
  private String university;
  private String major;
  private Integer graduationYear;

  public StudentEntity(String email, String password) {
    super(email, password);
  }

  public StudentEntity build(Map<String, Object> properties) {
    StudentEntity student = new StudentEntity();
    student.setFirstName(capitalizeFirstLetter((String) properties.get("firstName")));
    student.setLastName(capitalizeFirstLetter((String) properties.get("lastName")));
    student.setEmail((String) properties.get("email"));
    student.setPassword((String) properties.get("password"));
    return student;
  }
}
